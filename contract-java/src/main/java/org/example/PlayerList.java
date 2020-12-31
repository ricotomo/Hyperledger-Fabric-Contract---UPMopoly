/*
SPDX-License-Identifier: Apache-2.0
*/

package org.example;

import org.example.ledgerapi.StateList;
import org.hyperledger.fabric.contract.Context;

public class PlayerList {

    private StateList stateList;

    public PlayerList(Context ctx) {
        this.stateList = StateList.getStateList(ctx, PlayerList.class.getSimpleName(), Player::deserialize);
    }

    public PlayerList addPlayer(Player player) {
        stateList.addState(player);
        return this;
    }

    public Player getPlayer(String playerKey) {
        return (Player) this.stateList.getPlayer(playerKey);
    }

    public PlayerList updatePlayer(Player player) {
        this.stateList.updateState(player);
        return this;
    }
}
