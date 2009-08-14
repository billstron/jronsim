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
package thermostat;

import TranRunJLite.TrjSys;
import TranRunJLite.TrjTask;
import comMessage.Message;
import java.util.ArrayList;

/** This task manages communications for the thermostat.
 *
 * @author William Burke <billstron@gmail.com>
 */
public class ComTask extends TrjTask {

    private double dt;
    private double tNext;
    private ArrayList<Message> rxBuffer = new ArrayList<Message>();
    private ArrayList<Message> txQueue = new ArrayList<Message>();

    /** Construct the communications task.
     * 
     * @param name
     * @param sys
     * @param dt
     */
    public ComTask(String name, TrjSys sys, double dt) {
        super(name, sys, 0, true);
        this.dt = dt;
        this.tNext = 0;
    }

    /** Get the most recent message and remove it from the buffer.
     * 
     * @return
     */
    public Message getRxMsgLatest() {
        Message latest = null;
        if (rxBuffer.size() > 0) {
            int k = rxBuffer.size() - 1;
            latest = rxBuffer.get(k);
            rxBuffer.remove(k);
        }

        return latest;
    }

    /** Get the oldest message, remove it from the buffer, and shift the others
     * up to fill in its place.
     * 
     * @return
     */
    public Message getRxMsgOldest() {
        Message oldest = null;
        if (rxBuffer.size() > 0) {
            int k = 0;
            oldest = rxBuffer.get(k);
            rxBuffer.remove(k);
        }
        return oldest;
    }    

    /** Get the current number of messages in the message buffer.
     * 
     * @return
     */
    public int getRxMsgBufferSize(){
        return rxBuffer.size();
    }

    /** Enque new message for transmission.
     *
     * @param tx
     */
    public void enqueTxMsg(Message tx){
        txQueue.add(tx);
    }

    /** Run the communications task.
     * 
     * @param sys
     * @return
     */
    @Override
    public boolean RunTask(TrjSys sys) {
        double t = sys.GetRunningTime();
        if (t >= tNext) {
            // send all of the messages in the tx Queue
            while(txQueue.size() > 0){
                // TODO: send the message
                txQueue.remove(0);
            }
            // update the next timer
            tNext += dt;
        } else {
            if (runEntry) {
                nextState = currentState;
            }
        }
        return false;
    }
}
