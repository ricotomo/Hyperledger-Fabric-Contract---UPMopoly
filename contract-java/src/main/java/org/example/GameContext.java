package org.example;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;

class GameContext extends Context {

    public GameContext(ChaincodeStub stub) {
        super(stub);
        this.playerList = new PlayerList(this);
        this.facultyList = new FacultyList(this);
    }

    public PlayerList playerList;
    public FacultyList facultyList;
}