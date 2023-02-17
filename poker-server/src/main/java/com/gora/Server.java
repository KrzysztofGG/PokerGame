package com.gora;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class representing server connection
 */
public class Server {
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private static final int BUFFER_SIZE = 1024;
    private static Selector selector = null;
    //mapa wszystkich userow z ich unikatowymi kanalami
    public static Map<SocketChannel,String> connectedUsers=new HashMap<>();
    static int usersCount=0;
    static int maxPLayers=4;

    static int minPlayers=2;
    static int playersReady=0;
    static boolean gameStarted=false;

    static int playerMoveIndex;
    static String PlayerMove="";
    static GameLogic game;
    protected static Map<SocketChannel, String> clientsReady = new HashMap<>();
    public static void main(String[] args) {
        logger.log(Level.INFO,"starting server");
        try {
            InetAddress hostIP = InetAddress.getLocalHost();
            int port = 9999;
            String s = String.format("listening to connections on %s:%s ...%n",hostIP.getHostAddress(),port);
            logger.log(Level.INFO,s);
            selector = Selector.open();
            ServerSocketChannel mySocket = ServerSocketChannel.open();
            ServerSocket serverSocket = mySocket.socket();
            InetSocketAddress address = new InetSocketAddress(hostIP, port);
            serverSocket.bind(address);

            mySocket.configureBlocking(false);
            int ops = mySocket.validOps();
            mySocket.register(selector, ops, null);
            while (true) {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> i = selectedKeys.iterator();

                while (i.hasNext()) {
                    SelectionKey key = i.next();
                    if (key.isAcceptable() && usersCount <= maxPLayers) {
                        processAcceptEvent(mySocket);
                    }
                    else if (key.isReadable()) {
                        processReadEvent(key);
                    }
                    i.remove();
                }

                if(playersReady == usersCount && usersCount >= minPlayers && !gameStarted){
                    startGame();
                }
                if(gameStarted){
                    if(game.shouldGameEnd()){
                        endGame();
                        }

                    if(game.shouldRoundEnd()){
                        startNextRound();
                    }




                    if(game.shouldTurnEnd()){
                        game.nextTurn();
                        //figureOutWhoStarts();
                        if(game.bettingTurn == 2){
                            serverToAll("Swapping Cards\n");
                        }
                        else if(game.bettingTurn < 4){
                            serverToAll("betting Turn " + game.bettingTurn+"\n");
                            sendMoveInfo((SocketChannel)connectedUsers.keySet().toArray()[playerMoveIndex]);
                        }

                    }

                    if(game.cardSwapping){
                        if(game.shouldServerSendSwapInfo){
                            sendInfoCardSwapping();
                            game.shouldServerSendSwapInfo = false;
                        }
                        if(game.cardSwapping && game.shouldSwappingEnd()){
                            game.cardSwapping = false;
                            figureOutWhoStarts();
                            serverToAll("betting Turn " + game.bettingTurn + "\n");
                            sendMoveInfo((SocketChannel)connectedUsers.keySet().toArray()[playerMoveIndex]);
                        }
                    }

                    if(game.shouldRoundEnd()){
                        startNextRound();
                    }

                }

            }
        } catch (IOException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    /**
     * Method starting the game when 2 or more users ready
     */
    private static void startGame(){
        gameStarted=true;
        game = new GameLogic(connectedUsers);
        figureOutWhoStarts();
        game.nextRound();
        game.nextTurn();
        serverToAll("Game started\ngiving out cards");
        serverToAll("To check possible actions type \\actions\n");
        for(Map.Entry<SocketChannel, Player> entry : game.getClients().entrySet()){
            sendMessage(entry.getKey(), entry.getValue().printHand());
        }
        sendMoveInfo((SocketChannel)connectedUsers.keySet().toArray()[playerMoveIndex]);
    }
    /**
     * Method ending the game
     */
    private static void endGame(){
        gameStarted=false;
        serverToAll("Game ended\n");
        serverToAll("Winner is " + game.getWinner() + "\n");
        for(Map.Entry<SocketChannel, String> entry : connectedUsers.entrySet()){
            try {
                entry.getKey().close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        game.cardSwapping = false;
        game = null;
        gameStarted = false;
        playersReady = 0;
    }
    /**
     * Method starting next round when round ends
     */
    private static void startNextRound(){
        serverToAll(game.roundWinner.getName() + " won " + game.moneyOnTable + " with a " + game.roundWinner.getHand().printFigure()+"\n");
        game.nextRound();
        figureOutWhoStarts();
        serverToAll("Starting Round " + game.roundNumber +  "\n");
        game.nextTurn();
        sendMoveInfo((SocketChannel)connectedUsers.keySet().toArray()[playerMoveIndex]);
    }

    /**
     * Method finding out who starts the round
     */
    private static void figureOutWhoStarts(){
        for(Player p: game.getClients().values()){
            if(p.getRole() == Player.Role.SMALL_BLIND){
                PlayerMove = p.getName();
                break;
            }
        }
        for(int i=0; i<connectedUsers.size(); ++i){
            if(connectedUsers.values().toArray()[i].toString().equals(PlayerMove)){
                playerMoveIndex = i;
                if(game.getClients().get(connectedUsers.keySet().toArray()[playerMoveIndex]).getState() == Player.State.FOLD){
                    updatePlayerMove();
                    break;
                }

            }
        }
    }

    /**
     * Processes accepting player
     * @param mySocket - server socket
     * @throws IOException - when failed to accept player
     */
    private static void processAcceptEvent(ServerSocketChannel mySocket) throws IOException {
        usersCount+=1;
        // Accept the connection and make it non-blocking
        SocketChannel myClient = mySocket.accept();
        logger.log(Level.INFO, "connected");
        myClient.configureBlocking(false);
        //add accepted user socket to map to differ users
        connectedUsers.put(myClient,"User"+ usersCount);
        // Register interest in reading this channel
        myClient.register(selector, SelectionKey.OP_READ);
        greetUser(myClient);
    }

    /**
     * Updating player that moves
     */
    private static void updatePlayerMove(){
        do{
            playerMoveIndex++;
            playerMoveIndex = playerMoveIndex % usersCount;
            PlayerMove = connectedUsers.values().toArray()[playerMoveIndex].toString();
        }while(game.getClients().get(connectedUsers.keySet().toArray()[playerMoveIndex]).getState() == Player.State.FOLD);
        sendMessage((SocketChannel)connectedUsers.keySet().toArray()[playerMoveIndex], "It's your move\n");
    }

    /**
     * sends info to player that moves
     * @param myClient - player that moves
     */
    private static void sendMoveInfo(SocketChannel myClient){
        String message = "It's your move\n";
        sendMessage(myClient,message);
    }

    /**
     * greets user when connected
     * @param myClient -user
     */
    private static void greetUser(SocketChannel myClient) {
        String message="Welcome to the server "+connectedUsers.get(myClient) + "\n";
        message += "To get all commands type \\help \n";
        message += "type in \\ready to start the game \n";
        sendMessage(myClient,message);
    }

    /**
     * sends commands when user connected
     * @param myClient
     */
    private static void sendHelp(SocketChannel myClient) {
        String message="\\help - show all commands \n";
        message+="\\exit - disconnect from server \n";
        message += "\\ready - sets you as ready, game starts when every player is ready \n";
        sendMessage(myClient,message);
    }
    /**
     * sends info about possible actions
     * @param myClient - player
     */
    private static void sendActions(SocketChannel myClient) {
        String message = "\\my_hand - see your hand\n";
        message += "\\balance - see your account balance\n";
        message+="\\pool - show how much money is on the table \n";
        message +="\\exit - disconnect from server \n";
        if(connectedUsers.get(myClient).equals(PlayerMove)){
            message+="\\check - check a turn \n";
            message+="\\bet amount - bet a given amount of money\n";
            message += "\\fold - end your round \n";
            message += "\\all_in - bet all your money \n";
            message += "\\call - call the current bet \n";
        }

        sendMessage(myClient,message);
    }
    /**
     * sends info when user is ready
     */
    private static void userReady(SocketChannel myClient) {
        String message;
        if(!clientsReady.containsKey(myClient)){
            clientsReady.put(myClient,connectedUsers.get(myClient));
            playersReady+=1;
            message="You are ready to play \n";
            message+= "Waiting for " + (usersCount - playersReady) + " more players \n";
            sendToAllUsers(" is ready to play", myClient);
        }
        else
            message="You were already ready \n";
        sendMessage(myClient,message);
    }

    /**
     * processes user input
     * @param key - key of user sending data
     * @throws IOException - when failed to read data
     */
    private static void processReadEvent(SelectionKey key)
            throws IOException {
        logger.log(Level.INFO,"inside read");

        // create a ServerSocketChannel to read the request
        SocketChannel myClient = (SocketChannel) key.channel();

        // Set up out 1k buffer to read data into
        ByteBuffer clientResponse = ByteBuffer.allocate(BUFFER_SIZE);
        myClient.read(clientResponse);
        String data = new String(clientResponse.array()).trim();
        if(!gameStarted)
            readEvent(key, myClient, data);
        else {
            if(game.cardSwapping){
                if(game.getClients().get(myClient).getIsSwapping() && game.getClients().get(myClient).getState() != Player.State.FOLD){
                    readSwapping(key, myClient, data);
                }
                else {
                    sendMessage(myClient, "You are not swapping cards anymore, wait for others");
                }
            }
            else if (game.getClients().get(myClient).getState() != Player.State.FOLD) {
                readAction(key, myClient, data);
            } else {
                String message = "You have folded\n";
                sendMessage(myClient, message);
            }
        }
    }

    /**
     * sends information about how to swap cards to users
     */
    private static void sendInfoCardSwapping(){
        String message = "You can swap cards now \n";
        message += "To see your hand type \\my_hand\n";
        message += "To swap cards type \\swap i1,i2,i3\n";
        message+= "where i1,i2,i3 are indexes of cards you want to swap (1-5)\n";
        message += "if you don't want to swap type in \\end_swap \n";
        for(SocketChannel s: game.getClients().keySet()){
            if(game.getClients().get(s).getIsSwapping() && game.getClients().get(s).getState() != Player.State.FOLD)
                sendMessage(s, message);
        }
    }

    /**
     * processes swapping cards by user
     * @param key - key of user sending data
     * @param myClient - client to change cards
     * @param data - command from user
     */
    private static void readSwapping(SelectionKey key, SocketChannel myClient, String data){
        if(data.length() > 0){
            String wrongIndexes = "Wrong indexes \n";
            String message = "";
            String mess = String.format("swapping action received from {0}%n", connectedUsers.get(key.channel()));
            logger.log(Level.INFO, mess);
            if(data.startsWith("\\end_swap")){
                game.getClients().get(myClient).setIsSwapping(false);
                message = "Swapping ended";
            }
            else if(data.startsWith("\\my_hand")){
                processMyHand(myClient);
            }
            else if(data.startsWith("\\help")) sendInfoCardSwapping();
            else if(data.startsWith("\\swap")){
                String[] args = data.split(" ");
                if(args.length == 2){
                    String[] indexes = args[1].split(",");
                        ArrayList<Integer> indexesList = new ArrayList<>();
                        try{
                            for(String s: indexes){
                                indexesList.add(Integer.parseInt(s)-1);
                            }
                            boolean indexesOk = true;
                            boolean indexAmountOk = true;

                            if(indexesList.size() > 3) indexAmountOk = false;
                            for(int i: indexesList){
                                if(i<0 || i>4){
                                    indexesOk = false;
                                    break;
                                }
                            }
                            if(indexesOk){
                                if(indexAmountOk){
                                    game.swapCards(myClient,indexesList);
                                    game.getClients().get(myClient).setIsSwapping(false);
                                    message = "Cards swapped \n";
                                }
                                else{
                                    message = "You can swap only 3 cards \n";
                                }
                            }
                            else{
                                message = wrongIndexes;

                            }

                        }
                        catch (NumberFormatException e){
                            String error = wrongIndexes;
                            sendMessage(myClient,error);
                        }
                }
                else{
                    message = wrongIndexes;

                }
            }
            else{
                 message = "Invalid command \n";
            }
            sendMessage(myClient, message);
        }

    }

    /**
     * reads user input when game is in progress
     * @param key - key of user sending data
     * @param myClient - client socket of sender
     * @param data - command from user
     */

    private static void readAction(SelectionKey key, SocketChannel myClient, String data) throws IOException {
        if(data.length() > 0) {
            String notYourMove = "It's not your move \n";
            logger.log(Level.INFO, "action received from " + connectedUsers.get(key.channel()) + " " + data + "%n");
            if(game.isAllIn())
                sendMessage(myClient,"Player is all in you have to meet his bet \n fold or go all in as well \n");
            if (data.startsWith("\\pool"))
                processPool(myClient);
            else if (data.startsWith("\\balance"))
                processMyBalance(myClient);
            else if (data.startsWith("\\my_hand")) {
                processMyHand(myClient);
            } else if (data.startsWith("\\actions")) {
                sendActions(myClient);
            }
            else if(data.startsWith("\\exit")) {
                sendMessage(myClient, "You have left the game \n");
                myClient.close();
                connectedUsers.remove(myClient);
            }
            else if(data.startsWith("\\call")){
                if (connectedUsers.get(myClient).equals(PlayerMove))
                    processCall(myClient);
                else
                    sendMessage(myClient, notYourMove);
            }
            else if (data.startsWith("\\check")) {
                if (connectedUsers.get(myClient).equals(PlayerMove))
                    processCheck(myClient);
                else
                    sendMessage(myClient, notYourMove);
            }
            else if (data.startsWith("\\bet")) {
                if (connectedUsers.get(myClient).equals(PlayerMove))
                    processBet(myClient, data);
                else
                    sendMessage(myClient, notYourMove);
            }
            else if (data.startsWith("\\fold")) {
                if (connectedUsers.get(myClient).equals(PlayerMove))
                    processFold(myClient);
                else
                    sendMessage(myClient, notYourMove);

            }
            else if(data.startsWith("\\all_in")){
                if(connectedUsers.get(myClient).equals(PlayerMove))
                    processAllIn(myClient);
                else
                    sendMessage(myClient, notYourMove);
            }
            else{
                String message="Wrong command \n";
                sendMessage(myClient,message);
            }
        }

    }

    /**
     * processes  call command
     * @param myClient - user socket of the one sending command
     */
    private static void processCall(SocketChannel myClient){
        int bet = game.turnMaxBet - game.getClients().get(myClient).getCurrentBet();
        game.moneyOnTable += bet;
        betHelper(myClient, bet);
    }

    /**
     * sends info about bet stats
     * @param myClient - user socket of the one sending command
     * @param bet - amount of bet
     */
    private static void betHelper(SocketChannel myClient, int bet) {
        String message="Your total bet this turn "+ (bet + game.getClients().get(myClient).getCurrentBet()) +" \n";
        game.getClients().get(myClient).setCurrentBet(bet + game.getClients().get(myClient).getCurrentBet());
        game.getClients().get(myClient).removeFromBalance(bet);
        game.getClients().get(myClient).setState(Player.State.BET);
        sendMessage(myClient,message);
        sendToAllUsers(" bet "+bet, myClient);
        updatePlayerMove();
    }

    /**
     * processes asking for pool
     * @param myClient user to send info to
     */
    private static void processPool(SocketChannel myClient) {
        String message = "Money on table: " + game.getMoneyOnTable() + "\n";
        sendMessage(myClient, message);
    }
    /**
     * processes asking for balance
     * @param myClient user to send info to
     */
    private static void processMyBalance(SocketChannel myClient) {
        String message = "Your balance is " + game.getClients().get(myClient).getBalance() + "\n";
        sendMessage(myClient,message);
    }

    /**
     * processes checking
     * @param myClient user that checks
     */
    private static void processCheck(SocketChannel myClient) {
        String message;
        if(game.turnMaxBet == 0){
            message="You checked \n";
            game.getClients().get(myClient).setState(Player.State.CHECK);
            sendToAllUsers(" checked", myClient);
            updatePlayerMove();
        }
        else{
            message="You can't check other players have betted this turn \n";
        }
        sendMessage(myClient,message);
    }

    /**
     * processes all in event
     * @param myClient user that goes all in
     */
    private static void processAllIn(SocketChannel myClient){
        int playerBalance = game.getClients().get(myClient).getBalance();
        game.turnMaxBet = game.getClients().get(myClient).getCurrentBet() + playerBalance;
        game.moneyOnTable += playerBalance;
        String message = "You are all in!!!";
        game.getClients().get(myClient).setCurrentBet(playerBalance + game.getClients().get(myClient).getCurrentBet());
        game.getClients().get(myClient).removeFromBalance(playerBalance);
        game.getClients().get(myClient).setState(Player.State.ALLIN);
        sendMessage(myClient,message);
        sendToAllUsers(" is all in", myClient);
        updatePlayerMove();
    }

    /**
     * process betting event
     * @param myClient user that bets
     * @param data - command from user
     */
    private static void processBet(SocketChannel myClient, String data){
        String message;
        int bet;
        String[] parts = data.split(" ");
        if(parts.length == 2){
            try {
                bet = Integer.parseInt(parts[1]);
                if(bet == 0){
                    message = "You can't bet 0 \n";
                    sendMessage(myClient,message);
                    return;
                }
            }
            catch (NumberFormatException e){
                message = "Wrong bet \n";
                sendMessage(myClient,message);
                return;
            }
            if(game.getClients().get(myClient).getState() == Player.State.BET && game.turnMaxBet < bet + game.getClients().get(myClient).getCurrentBet()){
                message = "You can't raise anymore, you have already betted this turn \n";
                message += "To meet the bet type \\call\n";
            }
            else if(bet + game.getClients().get(myClient).getCurrentBet() >= game.turnMaxBet){
                if(bet <= game.getClients().get(myClient).getBalance()){
                    game.turnMaxBet = bet + game.getClients().get(myClient).getCurrentBet();
                    game.moneyOnTable += bet;
                    betHelper(myClient, bet);
                    return;
                }
                else{
                    message="You don't have enough money to bet that\n";
                }
            }
            else{
                int minBet = game.turnMaxBet - game.getClients().get(myClient).getCurrentBet();
                message="You can't bet less than "+minBet+" \n";
            }
        }
        else{
            message="Invalid bet value \n";
        }
        sendMessage(myClient,message);
    }
    /**
     * processes folding event
     * @param myClient user that folds
     */
    private static void processFold(SocketChannel myClient){
        game.getClients().get(myClient).setState(Player.State.FOLD);
        String message = "You folded";
        sendToAllUsers(" folded", myClient);
        updatePlayerMove();
        sendMessage(myClient ,message);
    }

    /**
     * processes asking for player hand
     * @param myClient user to send info to
     */
    private static void processMyHand(SocketChannel myClient) {
        String message;
        message = game.getClients().get(myClient).printHand();
        sendMessage(myClient,message);
    }

    /**
     * reads event before game started
     * @param key - key of the user
     * @param myClient - user socket
     * @param data - command from user
     */
    private static void readEvent(SelectionKey key, SocketChannel myClient, String data) throws IOException{
        if (data.length() > 0) {
            logger.info("message received from " + connectedUsers.get(key.channel()) + " " + data + "%n");
            if(data.startsWith("/all")){
                sendToAllUsers(data.substring(4),myClient);
            }
            else if(data.startsWith("\\help")) sendHelp(myClient);

            else if(data.startsWith("\\set_name")){
                String name=data.substring(9);
                String message;
                if(name.length()>0){
                    connectedUsers.put(myClient,name);
                    message="Your name has been changed to "+name;
                }
                else{
                    message="Wrong name\n";
                }
                sendMessage(myClient,message);
            }
            else if(data.startsWith("\\ready")) userReady(myClient);

            else if(data.startsWith("\\exit")) {
                myClient.close();
                connectedUsers.remove(myClient);
                logger.info("client disconnected");
            }
            else{
                String message="Wrong command type \\help to get all commands";
                sendMessage(myClient,message);
            }

        }
    }

    /**s
     * sends info to user
     * @param myClient user to send info to
     * @param message info to send
     */
    private static void sendMessage(SocketChannel myClient, String message) {
        ByteBuffer serverResponse= ByteBuffer.allocate(BUFFER_SIZE);
        serverResponse.put(message.getBytes());
        serverResponse.flip();
        try {
            myClient.write(serverResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * player sends info to all users
     * @param message info to send
     * @param messageAuthor user that sent the message
     */
    private static void sendToAllUsers(String message,SocketChannel messageAuthor){
        String finalMessage =connectedUsers.get(messageAuthor)+ message;
        connectedUsers.forEach((x, value)-> {
            try {
                ByteBuffer serverResponse= ByteBuffer.allocate(BUFFER_SIZE);
                serverResponse.put(finalMessage.getBytes());
                serverResponse.flip();
                if(x!=messageAuthor) {
                    x.write(serverResponse);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }
    /**
     * server sends info to all users
     * @param message info to send
     */
    private static void serverToAll(String message){
        connectedUsers.forEach((x, value)-> {
            try {
                ByteBuffer serverResponse= ByteBuffer.allocate(BUFFER_SIZE);
                serverResponse.put(message.getBytes());
                serverResponse.flip();
                x.write(serverResponse);


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }


}