package org.example;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;

class PlayerContext extends Context {

    public PlayerContext(ChaincodeStub stub) {
        super(stub);
        this.playerList = new PlayerList(this);
    }

    public PlayerList playerList;

}