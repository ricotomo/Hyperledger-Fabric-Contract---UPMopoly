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
//Creates player class
public class Player extends State {

    // States for players within UPMopoly game
    public final static String PLAYING = "PLAYING";
    public final static String ELIMINATED = "ELIMINATED";

    @Property()

    //getter and setter for state
    private String state="";

    public String getState() {
        return state;
    }

    public Player setState(String state) {
        this.state = state;
        return this;
    }

    @JSONPropertyIgnore()
    public boolean isPlaying() {
        return this.state.equals(Player.PLAYING);
    }

    @JSONPropertyIgnore()
    public boolean isEliminated() {
        return this.state.equals(Player.ELIMATED);
    }

    public Player setPlaying() {
        this.state = Player.PLAYING;
        return this;
    }

    public Player setElimated() {
        this.state = Player.ELIMINATED;
        return this;
    }


    @Property()
    private int playerNumber;

    @Property()
    private String name;

    @Property()
    private int initialAmount;


    public Player() {
        super();
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public Player setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
        return this;
    }

    public String getName() {
        return name;
    }

    public Player setName(String name) {
        this.name = name;
        return this;
    }

    public int getInitialAmount() {
        return initialAmount;
    }

    public Player setInitialAmount(int initialAmount) {
        this.initialAmount = initialAmount;
        return this;
    }


    @Override
    public String toString() {
        return "Player::" + this.getPlayerNumber() + " " + getName() + " " + getInitialAmount();
    }

    /**
     * Deserialize a state data to player
     *
     * @param {Buffer} data to form back into the object
     */

    public static Player deserialize(byte[] data) {
        JSONObject json = new JSONObject(new String(data, UTF_8));

        String name = json.getString("name");
        int playerNumber = json.getInt("playerNumber");
        int initialAmount = json.getInt("initialAmount");
        String state = json.getString("state");        
        return createInstance(name, playerNumber, initialAmount,state);
    }

    public static byte[] serialize(Player playerOne) {
        return State.serialize(playerOne);
    }

    /**
     * Factory method to create a commercial paper object
     */
    public static Player createInstance(String name, int playerNumber, 
            int initialAmount, String state) {
        return new Player().setName(name).setPlayerNumber(playerNumber)
                .setInitialAmount(initialAmount).setState(state);
    }


}
