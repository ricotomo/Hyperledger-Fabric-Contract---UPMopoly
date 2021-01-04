package org.example;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;

class FacultyContext extends Context {

    public FacultyContext(ChaincodeStub stub) {
        super(stub);
        this.facultyList = new FacultyList(this);
    }

    public FacultyList facultyList;

}