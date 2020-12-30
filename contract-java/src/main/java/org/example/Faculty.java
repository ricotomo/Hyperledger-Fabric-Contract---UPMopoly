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

    public CommercialPaper setFree() {
        this.state = Faculty.FREE;
        return this;
    }

    public CommercialPaper setBought() {
        this.state = Faculty.BOUGHT;
        return this;
    }

    @Property()
    private int ID;

    @Property()
    private String name;

    @Property()
    private float salePrice;

    @Property()
    private float rentalFee;

    public Faculty() {
        super();
    }

    //what'S this?
    public Faculty setKey() {
        this.key = State.makeKey(new String[] { this.ID });
        return this;
    }

    public String getID() {
        return ID;
    }

    public Faculty setID(int ID) {
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

    public float getSalePrice() {
        return salePrice;
    }

    public Faculty setSalePrice(float salePrice) {
        this.salePrice = salePrice;
        return this;
    }

    public float getRentalFee() {
        return rentalFee;
    }

    public Faculty setRentalFee(float rentalFee) {
        this.rentalFee = rentalFee;
        return this;
    }

    @Override
    public String toString() {
        return "Faculty::" + this.key + "   " + this.getID() + " " + getName() + " " + getSalePrice() + " " + getRentalPrice();
    }

    /**
     * Deserialize a state data to commercial paper
     *
     * @param {Buffer} data to form back into the object
     */

    public static Faculty deserialize(byte[] data) {
        JSONObject json = new JSONObject(new String(data, UTF_8));

        int ID = json.getInt("ID");
        String name = json.getString("name");
        float rentalFee = json.getFloat("rentalFee");
        float salePrice = json.getFloat("salePrice");
        String state = json.getString("state");        
        return createInstance(ID, name, rentalFee, salePrice,state);
    }

    public static byte[] serialize(Faculty aFaculty) {
        return State.serialize(aFaculty);
    }

    /**
     * Factory method to create a commercial paper object
     */
    public static Faculty createInstance(int ID, String name, float rentalFee,
            float salePrice, String state) {
        return new Faculty().setID(ID).setName(name).setRentalFee(rentalFee)
                .setSalePrice(salePrice).setKey().setState(state);
    }


}