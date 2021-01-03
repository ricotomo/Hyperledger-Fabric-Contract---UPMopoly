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
     * @param {GameContext} ctx the transaction context
     * @param {String} name of new player
     * @param {Integer} playerNumber player number for the game
     * @param {Integer} initialAmount initial amount of money for the player
     */
    @Transaction
    public Player newPlayer(GameContext ctx, String name, int playerNumber, int initialAmount) {

        System.out.println(ctx);

        // create an instance of the Player
        Player player = Player.createInstance(name, playerNumber, initialAmount, state);

        // Smart contract, rather than paper, moves player into PLAYING state
        player.setPlaying();

        System.out.println(player);
        // Add the paper to the list of all similar commercial papers in the ledger
        // world state
        ctx.playerList.addPlayer(player);

        // Must return a serialized paper to caller of smart contract
        return player;
    }
    
    /**
     * Creating faculty
     *
     * @param {GamerContext} ctx the transaction context
     * @param {Integer} ID of new new faculty
     * @param {String} name of new faculty
     * @param {Float} salePrice price of the faculty
     * @param {Float} rentalFee rental fee of faculty
     */
    @Transaction
    public Faculty newFaculty (FacultyContext ctx, int facultyID, String name, float salePrice, float rentalFee) {
    
        System.out.println(ctx);

        // create new instance of faculty
        Faculty faculty = Faculty.createInstance(facultyID, name, rentalFee, salePrice, state);

        // Moving faculty to Free state
        faculty.setFree();

        System.out.println(faculty);
        // Add the paper to the list of all similar commercial papers in the ledger
        // world state
        ctx.facultyList.addFaculty(faculty);

        // Must return a serialized paper to caller of smart contract
        return faculty;
        
    }
    
     /**
     * Buying a faculty
     *
     * @param {Context} ctx the transaction context
     * @param {Integer} playerNumber of the buyer
     * @param {Integer} facultyID of the faculty to be purchased
     */
    @Transaction
    public Faculty buyFaculty(FacultyContext ctx, int playerNumber, int facultyID) {
        
        // Retrieve the current paper using key fields provided
        String facultyKey = State.makeKey(new String[] {facultyID});
        Faculty faculty = ctx.FacultyList.getFaculty(facultyKey);

        // Validate availability of faculty
        if (!faculty.isBought()) {
            throw new RuntimeException("Faculty " + facultyID +  " is already owned by another player! ");
        }

        // First buy moves state from ISSUED to TRADING
        if (faculty.isFree()) {
            faculty.setBought();
            faculty.setOwnerNumber(playerNumber);
        }

        // Update the paper
        ctx.FacultyList.updateFaculty(faculty);
        return faculty;
    }
    
       /**
     * Paying rental
     *
     * @param {GameContext} ctxGame the transaction context
     * @param {FacultyContext} ctx faculty context
     * @param {Integer} facultyID of the faculty to be purchased
     * @param {Integer} ownerNumber of faculty owner
     * @param {Integer} visitorNumber of faculty visitor 
     */
    @Transaction
    public Player payRental (GameContext ctxGame, FacultyContext ctx, int facultyID, int ownerNumber, int visitorNumber) {
    
        String ownerKey = State.makeKey(new String[] {ownerNumber});
        Player owner = ctxGame.PlayerList.getPlayer(ownerKey);
        
        String visitorKey = State.makeKey(new String[] {playerNumber});
        Player visitor = ctxGame.PlayerList.getPlayer(visitorKey);
        
        String facultyKey = State.makeKey(new String[] {facultyID});
        Faculty faculty = ctxGame.FacultyList.getFaculty(facultyKey);
        
        Float feeToPay = faculty.getRentalFee();
        
        if (visitor.getInitialAmount() >= feeToPay) {
            visitor.setInitialAmount(visitor.getInitialAmount() -  feeToPay);
            owner.setInitialAmount(owner.getInitialAmount() + feeToPay);
        } else {
        visitor.setElimated();            
        throw new RuntimeException("Player" + " " + visitor.getName()+ " " + "don't have funds to pay the rental fee and got eliminated");
        }     
        
        ctx.PlayerList.updatePlayer(owner);
        ctx.PlayerList.updatePlayer(visitor);
        return visitor;
    }
     /**
     * Changing faculty owner (selling)
     *
     * @param {Context} ctx transaction context faculty
     * @param {Context} ctxGame transaction context
     * @param {Integer} facultyID of the faculty to be purchased
     * @param {Integer} ownerNumber of faculty owner
     * @param {Integer} buyerNumber of faculty buyer 
     * @param {Integer} salePrice of faculty 
     */
    @Transaction
    public Faculty facultySale(FacultyContext ctx, GameContext ctxGame, int facultyID, int ownerNumber, int buyerNumber, int salePrice) {
        
        // Retrieve the current paper using key fields provided
        String ownerKey = State.makeKey(new String[] {ownerNumber});
        Player owner = ctxGame.PlayerList.getPlayer(ownerKey);
        
        String buyerKey = State.makeKey(new String[] {buyerNumber});
        Player buyer = ctxGame.PlayerList.getPlayer(buyerKey);
        
        String facultyKey = State.makeKey(new String[] {facultyID});
        Faculty faculty = ctx.FacultyList.getFaculty(facultyKey);

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
        ctx.FacultyList.updateFaculty(faculty);
        ctxGame.PlayerList.updatePlayer(owner);
        ctxGame.PlayerList.updatePlayer(buyer);
        return faculty;
    }
    
      /**
     * Printing account balance of player
     *
     * @param {GameContext} ctx the transaction context
     * @param {Integer} playerNumber 
     */
    @Transaction
    public String printMoney (GameContext ctx, int playerNumber) {
    
        String playerKey = State.makeKey(new String[] {playerNumber});
        Player player = ctx.PlayerList.getPlayer(playerKey);
        
        return "Cash balance of" + " " + player.getName() + " " + "is" + " " +  player.getInitialAmount(); 
    }
      /**
     * Printing owner of faculty
     *
     * @param {FacultyContext} ctx the transaction context
     * @param @GameContext} ctxGame context of players
     * @param {Integer} facultyID 
     */
    @Transaction
    public String printOwner (FacultyContext ctx, GameContext ctxGame, int facultyID) {
    
        String facultyKey = State.makeKey(new String[] {facultyID});
        Faculty faculty = ctx.FacultyList.getFaculty(facultyKey);
        
        if (!faculty.isFree()) {
        String ownerKey = State.makeKey(new String[] {faculty.getOwnerNumber()});
        Player owner = ctxGame.PlayerList.getPlayer(ownerKey);
        return "Owner of faculty" + " " + faculty.getName() + " " + "is" + " " +  owner.getName(); 
        } else {
               return "Faculty"+ " " + faculty.getName() + "is free!";
        }
    }    
    
    /**
     * Printing all players still in game
     *
     */
    @Transaction
    public String printPlayers () {
    
    //?? good question
    }  
    /**
     * Buy commercial paper
     *
     * @param {Context} ctx the transaction context
     * @param {String} issuer commercial paper issuer
     * @param {Integer} paperNumber paper number for this issuer
     * @param {String} currentOwner current owner of paper
     * @param {String} newOwner new owner of paper
     * @param {Integer} price price paid for this paper
     * @param {String} purchaseDateTime time paper was purchased (i.e. traded)
     */
    @Transaction
    public CommercialPaper buy(CommercialPaperContext ctx, String issuer, String paperNumber, String currentOwner,
            String newOwner, int price, String purchaseDateTime) {

        // Retrieve the current paper using key fields provided
        String paperKey = State.makeKey(new String[] { paperNumber });
        CommercialPaper paper = ctx.paperList.getPaper(paperKey);

        // Validate current owner
        if (!paper.getOwner().equals(currentOwner)) {
            throw new RuntimeException("Paper " + issuer + paperNumber + " is not owned by " + currentOwner);
        }

        // First buy moves state from ISSUED to TRADING
        if (paper.isIssued()) {
            paper.setTrading();
        }

        // Check paper is not already REDEEMED
        if (paper.isTrading()) {
            paper.setOwner(newOwner);
        } else {
            throw new RuntimeException(
                    "Paper " + issuer + paperNumber + " is not trading. Current state = " + paper.getState());
        }

        // Update the paper
        ctx.paperList.updatePaper(paper);
        return paper;
    }

    /**
     * Redeem commercial paper
     *
     * @param {Context} ctx the transaction context
     * @param {String} issuer commercial paper issuer
     * @param {Integer} paperNumber paper number for this issuer
     * @param {String} redeemingOwner redeeming owner of paper
     * @param {String} redeemDateTime time paper was redeemed
     */
    @Transaction
    public CommercialPaper redeem(CommercialPaperContext ctx, String issuer, String paperNumber, String redeemingOwner,
            String redeemDateTime) {

        String paperKey = CommercialPaper.makeKey(new String[] { paperNumber });

        CommercialPaper paper = ctx.paperList.getPaper(paperKey);

        // Check paper is not REDEEMED
        if (paper.isRedeemed()) {
            throw new RuntimeException("Paper " + issuer + paperNumber + " already redeemed");
        }

        // Verify that the redeemer owns the commercial paper before redeeming it
        if (paper.getOwner().equals(redeemingOwner)) {
            paper.setOwner(paper.getIssuer());
            paper.setRedeemed();
        } else {
            throw new RuntimeException("Redeeming owner does not own paper" + issuer + paperNumber);
        }

        ctx.paperList.updatePaper(paper);
        return paper;
    }

}
