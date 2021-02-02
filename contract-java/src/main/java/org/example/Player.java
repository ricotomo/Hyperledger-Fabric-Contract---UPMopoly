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
    public String state="";

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
        return this.state.equals(Player.ELIMINATED);
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
    private String playerNumber;

    @Property()
    private String name;

    @Property()
    private int initialAmount;


    public Player() {
        super();
    }
    public Player setKey() {
        this.key = State.makeKey(new String[] { this.playerNumber });
        return this;
    }

    public String getPlayerNumber() {
        return playerNumber;
    }

    public Player setPlayerNumber(String playerNumber) {
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
        return "Player::" + this.key + "   " + this.getPlayerNumber() + " " + getName() + " " + getInitialAmount() + " " + getState();
    }

    /**
     * Deserialize a state data to player
     *
     * @param {Buffer} data to form back into the object
     */

    public static Player deserialize(byte[] data) {
        JSONObject json = new JSONObject(new String(data, UTF_8));

        String name = json.getString("name");
        String playerNumber = json.getString("playerNumber");
        int initialAmount = json.getInt("initialAmount");
        String state = json.getString("state");        
        return createInstance(name, playerNumber, initialAmount);
    }

    public static byte[] serialize(Player playerOne) {
        return State.serialize(playerOne);
    }

    public static Player createInstance(String name, String playerNumber, 
            int initialAmount) {
        return new Player().setName(name).setPlayerNumber(playerNumber)
                .setInitialAmount(initialAmount).setKey().setState(Player.PLAYING);
    }


}
