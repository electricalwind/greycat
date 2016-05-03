package org.mwg.ml.preprocessing;

import org.mwg.*;
import org.mwg.plugin.NodeFactory;
import org.mwg.maths.expression.KMathExpressionEngine;
import org.mwg.maths.expression.impl.MathExpressionEngine;
import org.mwg.plugin.AbstractNode;

public class MLMathNode extends AbstractNode {

    //Name of the algorithm to be used in the meta model
    public final static String NAME = "Math";

    //Factory of the class integrated
    public static class Factory implements NodeFactory {

        @Override
        public String name() {
            return NAME;
        }

        @Override
        public Node create(long world, long time, long id, Graph graph, long[] initialResolution) {
            return new MLMathNode(world, time, id, graph, initialResolution);
        }
    }

    private KMathExpressionEngine mathEngine;

    public MLMathNode(long p_world, long p_time, long p_id, Graph p_graph, long[] currentResolution) {
        super(p_world, p_time, p_id, p_graph, currentResolution);
        //mathEngine = new MathExpressionEngine();
    }


    @Override
    public Object get(String propertyName) {
        Object expressionObj = super.get(propertyName);
        if (propertyName.startsWith("$") && expressionObj != null && type(propertyName) == Type.STRING) {
            KMathExpressionEngine localEngine = MathExpressionEngine.parse(expressionObj.toString());
            return localEngine.eval(this);
        }
        return expressionObj;
    }

    @Override
    public void index(String indexName, Node nodeToIndex, String[] keyAttributes, Callback<Boolean> callback) {

    }

    @Override
    public void unindex(String indexName, Node nodeToIndex, String[] keyAttributes, Callback<Boolean> callback) {

    }


}