/*
SPDX-License-Identifier: Apache-2.0
*/
package org.example;

import java.util.HashSet;
import java.util.logging.Logger;
import org.example.ledgerapi.State;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

/**
 * A custom context provides easy access to list of all commercial papers
 */

/**
 * Define commercial paper smart contract by extending Fabric Contract class
 *
 */
@Contract(name = "org.papernet.commercialpaper", info = @Info(title = "MyAsset contract", description = "", version = "0.0.1", license = @License(name = "SPDX-License-Identifier: ", url = ""), contact = @Contact(email = "java-contract@example.com", name = "java-contract", url = "http://java-contract.me")))
@Default
public class GameContract implements ContractInterface {

    // use the classname for the logger, this way you can refactor
    private final static Logger LOG = Logger.getLogger(GameContract.class.getName());

    
    @Override
    public Context createContext(ChaincodeStub stub) {
        return new GameContext(stub);
    }

    public GameContract() {

    }

    /**
     * Define a custom context for commercial paper
     */

    /**
     * Instantiate to perform any setup of the ledger that might be required.
     *
     * @param {Context} ctx the transaction context
     */
    @Transaction
    public void instantiate(GameContext ctx) {
        // No implementation required with this example
        // It could be where data migration is performed, if necessary
        LOG.info("No data migration to perform");
    }

    /**
     * Creating player
     *
     * @param {Context} ctx the transaction context
     * @param {String} name of new player
     * @param {Integer} playerNumber player number for the game
     * @param {Integer} initialAmount initial amount of money for the player
     */
    @Transaction
    public Player newPlayer(GameContext ctx, String name, String playerNumber, int initialAmount) {

        System.out.println(ctx);

        // create an instance of the Player
        Player player = Player.createInstance(name, playerNumber, initialAmount);

        // Smart contract, rather than paper, moves player into PLAYING state
        player.setPlaying();

        System.out.println(player);
        // Add the paper to the list of all similar commercial papers in the ledger
        // world state
        ctx.playerList.addPlayer(player);

        return player;
    }
    
    /**
     * Creating faculty
     *
     * @param {Context} ctx the transaction context
     * @param {String} ID of new new faculty
     * @param {String} name of new faculty
     * @param {Integer} salePrice price of the faculty
     * @param {Integer} rentalFee rental fee of faculty
     */
    @Transaction
    public Faculty newFaculty (GameContext ctx, String facultyID, String name, int salePrice, int rentalFee) {
    
        System.out.println(ctx);

        // create new instance of faculty
        Faculty faculty = Faculty.createInstance(facultyID, name, rentalFee, salePrice);

        // Moving faculty to Free state
        faculty.setFree();

        System.out.println(faculty);
        ctx.facultyList.addFaculty(faculty);

        // Must return a serialized paper to caller of smart contract
        return faculty;
        
    }
    
     /**
     * Buying a faculty
     *
     * @param {Context} ctx the transaction context
     * @param {String} playerNumber of the buyer
     * @param {String} facultyID of the faculty to be purchased
     */
    @Transaction
    public Faculty buyFaculty(GameContext ctx, String playerNumber, String facultyID) {
        
        // Retrieve the current paper using key fields provided
        String facultyKey = State.makeKey(new String[] {facultyID});
        Faculty faculty = ctx.facultyList.getFaculty(facultyKey);
		
		String buyerKey = State.makeKey(new String[] {playerNumber});
        Player buyer = ctx.playerList.getPlayer(buyerKey);

        // Validate availability of faculty
        if (!faculty.isFree()) {
        throw new RuntimeException("Faculty " + facultyID +  " is already owned by another player! ");
        }
		if (buyer.getInitialAmount() < faculty.getSalePrice()) {
	    throw new RuntimeException("Player haven't got enough fund to buy faculty");
		}
		
        if (faculty.isFree()) {
            faculty.setBought();
            faculty.setOwnerNumber(playerNumber);
			buyer.setInitialAmount(buyer.getInitialAmount() - faculty.getSalePrice());
        }

        ctx.facultyList.updateFaculty(faculty);
		ctx.playerList.updatePlayer(buyer);
		
        return faculty;
    }
    
       /**
     * Paying rental
     *
     * @param {Context} ctx the transaction context
     * @param {String} facultyID of the faculty to be purchased
     * @param {String} ownerNumber of faculty owner
     * @param {String} visitorNumber of faculty visitor 
     */
    @Transaction
    public Player payRental (GameContext ctx, String facultyID, String ownerNumber, String visitorNumber) {
    
        String ownerKey = State.makeKey(new String[] {ownerNumber});
        Player owner = ctx.playerList.getPlayer(ownerKey);
        
        String visitorKey = State.makeKey(new String[] {visitorNumber});
        Player visitor = ctx.playerList.getPlayer(visitorKey);
        
        String facultyKey = State.makeKey(new String[] {facultyID});
        Faculty faculty = ctx.facultyList.getFaculty(facultyKey);
        
        int feeToPay = faculty.getRentalFee();
        
        if (visitor.getInitialAmount() >= feeToPay) {
            visitor.setInitialAmount(visitor.getInitialAmount() -  feeToPay);
            owner.setInitialAmount(owner.getInitialAmount() + feeToPay);
        } else {
        visitor.setElimated();            
        throw new RuntimeException("Player" + " " + visitor.getName()+ " " + "don't have funds to pay the rental fee and got eliminated");
        }     
        
        ctx.playerList.updatePlayer(owner);
        ctx.playerList.updatePlayer(visitor);
        return visitor;
    }
     /**
     * Changing faculty owner (selling)
     *

     * @param {Context} ctx transaction context
     * @param {String} facultyID of the faculty to be purchased
     * @param {String} ownerNumber of faculty owner
     * @param {String} buyerNumber of faculty buyer 
     * @param {Integer} salePrice of faculty 
     */
    @Transaction
    public Faculty facultySale(GameContext ctx, String facultyID, String ownerNumber, String buyerNumber, int salePrice) {
        
        // Retrieve the current paper using key fields provided
        String ownerKey = State.makeKey(new String[] {ownerNumber});
        Player owner = ctx.playerList.getPlayer(ownerKey);
        
        String buyerKey = State.makeKey(new String[] {buyerNumber});
        Player buyer = ctx.playerList.getPlayer(buyerKey);
        
        String facultyKey = State.makeKey(new String[] {facultyID});
        Faculty faculty = ctx.facultyList.getFaculty(facultyKey);

        if (buyer.getInitialAmount() < salePrice) {
        throw new RuntimeException("Unavailable funds to buy faculty!");
        }
        
        // Validate availability of faculty
        if (faculty.getOwnerNumber() == ownerNumber) {
            faculty.setOwnerNumber(buyerNumber);
            owner.setInitialAmount(owner.getInitialAmount() + salePrice);
            buyer.setInitialAmount(buyer.getInitialAmount() - salePrice);
        }
        // Update the paper
        ctx.facultyList.updateFaculty(faculty);
        ctx.playerList.updatePlayer(owner);
        ctx.playerList.updatePlayer(buyer);
        return faculty;
    }
    
      /**
     * Printing account balance of player
     *
     * @param {Context} ctx the transaction context
     * @param {String} playerNumber 
     */
    @Transaction
    public String printMoney (GameContext ctx, String playerNumber) {
    
        String playerKey = State.makeKey(new String[] {playerNumber});
        Player player = ctx.playerList.getPlayer(playerKey);
        
        return "Cash balance of" + " " + player.getName() + " " + "is" + " " +  player.getInitialAmount(); 
    }
      /**
     * Printing owner of faculty
     *
     * @param {Context} ctx 
     * @param {String} facultyID 
     */
    @Transaction
    public String printOwner (GameContext ctx, String facultyID) {
    
        String facultyKey = State.makeKey(new String[] {facultyID});
        Faculty faculty = ctx.facultyList.getFaculty(facultyKey);
        
        if (!faculty.isFree()) {
        String ownerKey = State.makeKey(new String[] {faculty.getOwnerNumber()});
        Player owner = ctx.playerList.getPlayer(ownerKey);
        return "Owner of faculty" + " " + faculty.getName() + " " + "is" + " " +  owner.getName(); 
        } else {
               return "Faculty"+ " " + faculty.getName() + "is free!";
        }
    }	
	/**
     * Retrieve all players still in game
     *
     * @param ctx the transaction context
     * @return array of active players
     */
   	@Transaction()
	public String printPlayers(GameContext ctx){
	 StringBuilder playerStream = new StringBuilder();
	 for( int id =0; id<100;id++){
		String playerNumber = "no" + Integer.toString(id);
		try{
		String playerKey = State.makeKey(new String[] {playerNumber});
        Player player = ctx.playerList.getPlayer(playerKey);
		if (player.getState() != "ELIMINATED"){
		playerStream.append(player.getName());
		playerStream.append(" ");			 
		}
		}
		catch (Exception e) {
		System.out.println("No such player!");
		}
	 }
	 return "Players still in game: " + playerStream.toString();
	}
}
