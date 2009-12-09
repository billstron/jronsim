/*
 * Copyright (c) 2009, Regents of the University of California
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *  * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  * Neither the name of the University of California, Berkeley
 * nor the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package house.userInterface;

import java.util.List;
import javax.swing.SwingWorker;

/** The UserInterfaceWorker object continually pulls data from the UserInterface
 * Task and returns it to the UserInterface GUI.
 * 
 * @author William Burke <billstron@gmail.com>
 */
public class UserInterfaceWorker extends SwingWorker<Void, UserInterfaceData> {

    private UserInterfaceJFrame ui;
    private UserInterfaceIO io;

    /** Construct the User Interface Worker.
     * 
     * @param ui -- The User Interface GUI Object.
     * @param io -- The UserInterface IO Object (Task). 
     */
    public UserInterfaceWorker(UserInterfaceJFrame ui, UserInterfaceIO io) {
        super();
        this.ui = ui;
        this.io = io;
    }

    /** Continually get the data in the background.
     *
     * @return
     * @throws Exception
     */
    @Override
    protected Void doInBackground() throws Exception {

        while (!isCancelled()) {
            publish(new UserInterfaceData(io.getAuxDisplay(),
                    io.getMainDisplay(), io.getAuxLabel(),
                    io.getHoldLed(), io.getHeaterLed(), io.getCoolerLed()));
        }
        return null;
    }

    /** Set the GUI based on the latest data pulled from the IO object.
     * 
     * @param data
     */
    @Override
    protected void process(List<UserInterfaceData> data) {

        UserInterfaceData datum = data.get(data.size() - 1);

        ui.setAuxMessage(datum.msgAux);
        ui.setMainMessage(datum.msgMain);
        ui.setAuxLabel(datum.labelAuxMsg);
        ui.setHoldLed(datum.ledHold);
        ui.setHeaterLed(datum.ledHeater);
        ui.setCoolerLed(datum.ledCooler);
    }
}
