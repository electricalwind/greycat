package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Node;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;

import static org.mwg.task.Actions.fromIndexAll;

public class ActionWithoutTest extends AbstractActionTest {

    @Test
    public void test() {
        initGraph();
        fromIndexAll("nodes")
                .selectWithout("name", "n0")
                .then(new Action() {
                    @Override
                    public void eval(TaskContext context) {
                        Assert.assertEquals(((Node[]) context.result())[0].get("name"), "n1");
                        Assert.assertEquals(((Node[]) context.result())[1].get("name"), "root");
                    }
                })
                .execute(graph);

        fromIndexAll("nodes")
                .selectWithout("name", "n.*")
                .then(new Action() {
                    @Override
                    public void eval(TaskContext context) {
                        Assert.assertEquals(((Node[]) context.result())[0].get("name"), "root");
                    }
                })
                .execute(graph);
        removeGraph();

    }

}
