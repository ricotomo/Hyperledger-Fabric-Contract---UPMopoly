/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.example;

import static java.nio.charset.StandardCharsets.UTF_8;

import org.example.ledgerapi.State;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONObject;
import org.json.JSONPropertyIgnore;

@DataType()
public class Faculty extends State {

    // TO BE DELETED
    public final static String FREE = "FREE";
    public final static String BOUGHT = "BOUGHT";

    @Property()
    //getter and setter for state
    private String state="";

    public String getState() {
        return state;
    }

    public Faculty setState(String state) {
        this.state = state;
        return this;
    }

    @JSONPropertyIgnore()
    public boolean isFree() {
        return this.state.equals(Faculty.FREE);
    }

    @JSONPropertyIgnore()
    public boolean isBought() {
        return this.state.equals(Faculty.BOUGHT);
    }

    public Faculty setFree() {
        this.state = Faculty.FREE;
        return this;
    }

    public Faculty setBought() {
        this.state = Faculty.BOUGHT;
        return this;
    }

    @Property()
    private String ID;

    @Property()
    private String name;

    @Property()
    private int salePrice;

    @Property()
    private int rentalFee;
    
    @Property()
    private String ownerNumber = "";

    public Faculty() {
        super();
    }

    //what'S this?
    //will this throw an error due to type differences?
    public Faculty setKey() {
        this.key = State.makeKey(new String[] { this.ID });
        return this;
    }

    public String getID() {
        return ID;
    }

    public Faculty setID(String ID) {
        this.ID = ID;
        return this;
    }

    public String getName() {
        return name;
    }

    public Faculty setName(String name) {
        this.name = name;
        return this;
    }

    public int getSalePrice() {
        return salePrice;
    }

    public Faculty setSalePrice(int salePrice) {
        this.salePrice = salePrice;
        return this;
    }

    public int getRentalFee() {
        return rentalFee;
    }

    public Faculty setRentalFee(int rentalFee) {
        this.rentalFee = rentalFee;
        return this;
    }
    
    public String getOwnerNumber() {
        return ownerNumber;
    }

    public Faculty setOwnerNumber(String ownerNumber) {
        this.ownerNumber = ownerNumber;
        return this;
    }
    @Override
    public String toString() {
        return "Faculty::" + this.key + "   " + this.getID() + " " + getName() + " " + getSalePrice() + " " + getRentalFee() + " " + getOwnerNumber();
    }

    /**
     * Deserialize a state data to commercial paper
     *
     * @param {Buffer} data to form back into the object
     */

    public static Faculty deserialize(byte[] data) {
        JSONObject json = new JSONObject(new String(data, UTF_8));

        String ID = json.getString("ID");
        String name = json.getString("name");
        int rentalFee = json.getInt("rentalFee");
        int salePrice = json.getInt("salePrice");
        String ownerNumber = json.getString("ownerNumber");
        String state = json.getString("state");        
        return createInstance(ID, name, rentalFee, salePrice,state);
    }

    public static byte[] serialize(Faculty aFaculty) {
        return State.serialize(aFaculty);
    }

    /**
     * Factory method to create a commercial paper object
     */
    //for owner, default should be null (free to buy)
    public static Faculty createInstance(String ID, String name, int rentalFee,
            int salePrice, String state) {
        return new Faculty().setID(ID).setName(name).setRentalFee(rentalFee)
                .setSalePrice(salePrice).setKey().setState(state);
    }


}
