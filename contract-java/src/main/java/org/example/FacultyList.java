/*
SPDX-License-Identifier: Apache-2.0
*/

package org.example;

import org.example.ledgerapi.StateList;
import org.hyperledger.fabric.contract.Context;

public class FacultyList {

    private StateList stateList;

    public FacultyList(Context ctx) {
        this.stateList = StateList.getStateList(ctx, FacultyList.class.getSimpleName(), Faculty::deserialize);
    }
    
    public FacultyList addFaculty(Faculty faculty) {
        stateList.addState(faculty);
        return this;
    }

    public Faculty getFaculty(String facultyKey) {
        return (Faculty) this.stateList.getState(facultyKey);
    }

    public FacultyList updateFaculty(Faculty faculty) {
        this.stateList.updateState(faculty);
        return this;
    }
}
