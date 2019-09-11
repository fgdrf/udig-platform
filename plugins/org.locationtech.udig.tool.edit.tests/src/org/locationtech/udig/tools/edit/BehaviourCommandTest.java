/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.command.Command;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.internal.Map;

public class BehaviourCommandTest {

    @Test
    public void testRun() throws Exception {
        RunBehaviour runBehaviour = new RunBehaviour();
        RunBehaviour runBehaviour2 = new RunBehaviour();
        List<Behaviour> list = new ArrayList<Behaviour>();

        list.add(runBehaviour);
        list.add(runBehaviour2);
        list.add(new NoRunBehaviour());

        BehaviourCommand command = new BehaviourCommand(list, null);

        assertFalse(runBehaviour.ran);
        assertFalse(runBehaviour2.ran);

        NullProgressMonitor nullProgressMonitor = new NullProgressMonitor();
        command.execute(nullProgressMonitor);

        assertTrue(runBehaviour.ran);
        assertTrue(runBehaviour2.ran);

        nullProgressMonitor = new NullProgressMonitor();
        command.rollback(nullProgressMonitor);
        assertFalse(runBehaviour.ran);
        assertFalse(runBehaviour2.ran);

    }

    @Test
    public void testInOrderRunAndRollback() throws Exception {
        AtomicInteger sharedState = new AtomicInteger(0);
        RunBehaviour runBehaviour = new RunBehaviour(sharedState);
        RunBehaviour runBehaviour2 = new RunBehaviour(sharedState);
        List<Behaviour> list = new ArrayList<Behaviour>();

        list.add(runBehaviour);
        list.add(runBehaviour2);

        BehaviourCommand command = new BehaviourCommand(list, null);
        assertFalse(runBehaviour.ran);
        assertFalse(runBehaviour2.ran);

        NullProgressMonitor nullProgressMonitor = new NullProgressMonitor();
        command.execute(nullProgressMonitor);

        assertTrue(runBehaviour.ran);
        assertTrue(runBehaviour2.ran);
        assertTrue(runBehaviour2.internalState > runBehaviour.internalState);

        nullProgressMonitor = new NullProgressMonitor();
        command.rollback(nullProgressMonitor);
        assertFalse(runBehaviour.ran);
        assertFalse(runBehaviour2.ran);
        assertFalse(runBehaviour2.internalState > runBehaviour.internalState);

    }

    class RunBehaviour implements Behaviour {
        int id = 0;

        boolean ran;

        private AtomicInteger counter;

        int internalState = 0;

        public RunBehaviour(AtomicInteger sharedState) {
            this.counter = sharedState;
        }

        public RunBehaviour() {
            this(new AtomicInteger());
        }

        public boolean isValid(EditToolHandler handler) {
            return true;
        }

        public UndoableMapCommand getCommand(EditToolHandler handler) {
            return new UndoableMapCommand() {

                private Map map;

                public void setMap(IMap map) {
                    this.map = (Map) map;
                }

                public Map getMap() {
                    return map;
                }

                public void run(IProgressMonitor monitor) throws Exception {
                    if (ran)
                        fail();
                    internalState = counter.incrementAndGet();
                    ran = true;
                }

                public Command copy() {
                    return null;
                }

                public String getName() {
                    return "Run Command"; //$NON-NLS-1$
                }

                public void rollback(IProgressMonitor monitor) throws Exception {
                    if (!ran)
                        fail();
                    internalState = counter.incrementAndGet();
                    ran = false;
                }
            };
        }

        public void handleError(EditToolHandler handler, Throwable error,
                UndoableMapCommand command) {
            fail();
        }

    }

    class NoRunBehaviour implements Behaviour {

        public boolean isValid(EditToolHandler handler) {
            return false;
        }

        public UndoableMapCommand getCommand(EditToolHandler handler) {
            fail();
            return null;
        }

        public void handleError(EditToolHandler handler, Throwable error,
                UndoableMapCommand command) {
            fail();
        }

    }

}
