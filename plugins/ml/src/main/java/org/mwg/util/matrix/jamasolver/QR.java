package org.mwg.util.matrix.jamasolver;


import org.mwg.util.matrix.KMatrix;

/** QR Decomposition.
<P>
   For an m-by-n matrix A with m >= n, the QR decomposition is an m-by-n
   orthogonal matrix Q and an n-by-n upper triangular matrix R so that
   A = Q*R.
<P>
   The QR decompostion always exists, even if the matrix does not have
   full rank, so the constructor will never fail.  The primary use of the
   QR decomposition is in the least squares solution of nonsquare systems
   of simultaneous linear equations.  This will fail if isFullRank()
   returns false.
*/

public class QR implements java.io.Serializable {

/* ------------------------
   Class variables
 * ------------------------ */

   /** Array for internal storage of decomposition.
   @serial internal array storage.
   */
   private double[][] QR;

   /** Row and column dimensions.
   @serial column dimension.
   @serial row dimension.
   */
   private int m, n;

   /** Array for internal storage of diagonal of R.
   @serial diagonal of R.
   */
   private double[] Rdiag;

/* ------------------------
   Constructor
 * ------------------------ */

   /** QR Decomposition, computed by Householder reflections.
       Structure to access R and the Householder vectors and compute Q.
   @param A    Rectangular matrix
   */

   public QR(KMatrix A) {
      // Initialize.
      QR = Utils.convertToArray(A);
      m = A.rows();
      n = A.columns();
      Rdiag = new double[n];

      // Main loop.
      for (int k = 0; k < n; k++) {
         // Compute 2-norm of k-th column without under/overflow.
         double nrm = 0;
         for (int i = k; i < m; i++) {
            nrm = Utils.hypot(nrm,QR[i][k]);
         }

         if (nrm != 0.0) {
            // Form k-th Householder vector.
            if (QR[k][k] < 0) {
               nrm = -nrm;
            }
            for (int i = k; i < m; i++) {
               QR[i][k] /= nrm;
            }
            QR[k][k] += 1.0;

            // Apply transformation to remaining columns.
            for (int j = k+1; j < n; j++) {
               double s = 0.0; 
               for (int i = k; i < m; i++) {
                  s += QR[i][k]*QR[i][j];
               }
               s = -s/QR[k][k];
               for (int i = k; i < m; i++) {
                  QR[i][j] += s*QR[i][k];
               }
            }
         }
         Rdiag[k] = -nrm;
      }
   }

/* ------------------------
   Public Methods
 * ------------------------ */

   /** Is the matrix full rank?
   @return     true if R, and hence A, has full rank.
   */

   public boolean isFullRank () {
      for (int j = 0; j < n; j++) {
         if (Rdiag[j] == 0)
            return false;
      }
      return true;
   }

   /** Return the Householder vectors
   @return     Lower trapezoidal matrix whose columns define the reflections
   */

   public KMatrix getH () {
      KMatrix H=new KMatrix(null,m,n);
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            if (i >= j) {
               H.set(i,j, QR[i][j]);
            } else {
               H.set(i,j,0.0);
            }
         }
      }
      return H;
   }

   /** Return the upper triangular factor
   @return     R
   */

   public KMatrix getR () {
      KMatrix R = new KMatrix(null,n,n);
      for (int i = 0; i < n; i++) {
         for (int j = 0; j < n; j++) {
            if (i < j) {
               R.set(i,j,QR[i][j]);
            } else if (i == j) {
               R.set(i,j,Rdiag[i]);
            } else {
               R.set(i,j, 0.0);
            }
         }
      }
      return R;
   }

   /** Generate and return the (economy-sized) orthogonal factor
   @return     Q
   */

   public KMatrix getQ () {
      KMatrix Q = new KMatrix(null,m,n);
      for (int k = n-1; k >= 0; k--) {
         for (int i = 0; i < m; i++) {
            Q.set(i,k, 0.0);
         }
         Q.set(k,k, 1.0);
         for (int j = k; j < n; j++) {
            if (QR[k][k] != 0) {
               double s = 0.0;
               for (int i = k; i < m; i++) {
                  s += QR[i][k]*Q.get(i,j);
               }
               s = -s/QR[k][k];
               for (int i = k; i < m; i++) {
                  Q.add(i,j, s*QR[i][k]);
               }
            }
         }
      }
      return Q;
   }

   /** Least squares solution of A*X = B
   @param B    A Matrix with as many rows as A and any number of columns.
   @return     X that minimizes the two norm of Q*R*X-B.
   @exception  IllegalArgumentException  Matrix row dimensions must agree.
   @exception  RuntimeException  Matrix is rank deficient.
   */

   public KMatrix solve (KMatrix B) {
      if (B.rows() != m) {
         throw new IllegalArgumentException("Matrix row dimensions must agree.");
      }
      if (!this.isFullRank()) {
         throw new RuntimeException("Matrix is rank deficient.");
      }
      
      // Copy right hand side
      int nx = B.columns();
      double[][] X = Utils.convertToArray(B);

      // Compute Y = transpose(Q)*B
      for (int k = 0; k < n; k++) {
         for (int j = 0; j < nx; j++) {
            double s = 0.0; 
            for (int i = k; i < m; i++) {
               s += QR[i][k]*X[i][j];
            }
            s = -s/QR[k][k];
            for (int i = k; i < m; i++) {
               X[i][j] += s*QR[i][k];
            }
         }
      }
      // Solve R*X = Y;
      for (int k = n-1; k >= 0; k--) {
         for (int j = 0; j < nx; j++) {
            X[k][j] /= Rdiag[k];
         }
         for (int i = 0; i < k; i++) {
            for (int j = 0; j < nx; j++) {
               X[i][j] -= X[k][j]*QR[i][k];
            }
         }
      }

      KMatrix res = Utils.convertToMatrix(X);
      return (getMatrix(res,0,n-1,0,nx-1));
   }

   private static KMatrix getMatrix (KMatrix B, int i0, int i1, int j0, int j1) {

      KMatrix X = new KMatrix(null, i1-i0+1,j1-j0+1);
      try {
         for (int i = i0; i <= i1; i++) {
            for (int j = j0; j <= j1; j++) {
               X.set(i-i0,j-j0, B.get(i,j));
            }
         }
      } catch(ArrayIndexOutOfBoundsException e) {
         throw new ArrayIndexOutOfBoundsException("Submatrix indices");
      }
      return X;
   }

  private static final long serialVersionUID = 1;
}