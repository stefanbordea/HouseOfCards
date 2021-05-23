package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML private VBox vbox;
    @FXML private Label currentHouseTotal;
    @FXML private Label playerPoints;
    @FXML private Label cardsLeft;
    @FXML
    private ImageView house1Image;
    @FXML
    private ImageView house2Image;
    @FXML
    private ImageView house3Image;
    @FXML
    private ImageView house4Image;
    @FXML
    private Label house1Total;
    @FXML
    private Label house2Total;
    @FXML
    private Label house3Total;
    @FXML
    private Label house4Total;
    @FXML
    private ImageView currentCard;
    private static Image defaultHouseImage = new Image(Controller.class.getResourceAsStream("/resources/images/houseDefault.png"));
    private static Image stopImage = new Image(Controller.class.getResourceAsStream("/resources/images/stop.png"));
    private static Game game = new Game();
    private static BackgroundSize backgroundSize = new BackgroundSize(500,
            500,
            false,
            false,
            false,
            true);
    private static BackgroundImage image = new BackgroundImage(new Image("/resources/images/background.jpeg"),
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            backgroundSize);
    @FXML private int currentCardValue;


    public void placeCard(MouseEvent mouseEvent) throws IOException {

        ImageView imageView = (ImageView) mouseEvent.getSource();
            // if statements used when clicking a house image
            if (imageView == house1Image) {
                playRound(0, house1Total, house1Image);
                currentHouseTotal = house1Total;
            } else if (imageView == house2Image) {
                playRound(1, house2Total, house2Image);
                currentHouseTotal = house2Total;
            } else if (imageView == house3Image) {
                playRound(2, house3Total, house3Image);
                currentHouseTotal = house3Total;
            } else if (imageView == house4Image) {
                playRound(3, house4Total, house4Image);
                currentHouseTotal = house4Total;
            }


        clearHouseIfPlayerScoresAndAddPoints(currentHouseTotal, imageView); // clear house and award player
        closeHouse(game.getPlayer().getChoice(), imageView); // close house when over 31


        //draw a new window when player wins/loses and ask to play again or exit, also make images unclickable
        if (house1Image.isDisable() && house2Image.isDisable() && house3Image.isDisable() && house4Image.isDisable()) {
            disableImages(true);
            gameEnd(false);
        } else if (game.getRound() == 40) {
            disableImages(true);
            gameEnd(true);
            currentCard.setImage(defaultHouseImage);
            cardsLeft.setText("Cards left: " + "0");
        }
        if(game.getRound() < 40){ // draws a card and shows it only if round < 40
            showCurrentCard();

        }

    }

    public void showCurrentCard() {
        game.getPlayer().setCurrentCard(game.getDeck().drawCard()); // draws a card from the deck arraylist and sets it as the current card
        cardsLeft.setText("Cards left: " + (game.cardsLeft() + 1)); // when card is drawn, decrease card counter by 1
        currentCard.setImage(game.getDeck().getDeckImages()[game.getCardIndex()]); // shows the card that is drawn from the deck arraylist
    }

    public void replaceCard(ImageView imageView) { //sets the card in the chosen house
        imageView.setImage(game.getDeck().getDeckImages()[game.getCardIndex()]);
    }

    public void playRound(int choice, Label houseTotal, ImageView houseImage){ // sets the total amount on each house
        game.getPlayer().setChoice(choice);
        replaceCard(houseImage);
        currentCardValue = game.getBoard()[game.getPlayer().getChoice()].addPoints(game.getPlayer().getCurrentCard().getValue());
        houseTotal.setText("House " + (choice + 1) + " \ntotal: "  + game.getBoard()[choice].calculateSumOfHouse());
    }

    public void closeHouse(int choice, ImageView houseImage){ // closes the house, changes the image and makes it disabled
        if(game.getBoard()[choice].isClosed()){
            houseImage.setImage(stopImage);
            houseImage.setDisable(true);
        }
    }

    public void clearHouseIfPlayerScoresAndAddPoints(Label label, ImageView imageView){
        if(game.getBoard()[game.getPlayer().getChoice()].calculateSumOfHouse() == 31){  //when a house goes 31
            game.getBoard()[game.getPlayer().getChoice()].clearHouse();                // clear that house
            game.getPlayer().addPointsToPlayer();                                     // add 100 points to the player
            playerPoints.setText("Player points: " + game.getPlayer().getPoints());  // change the label of the player's score
            label.setText("House " + (game.getPlayer().getChoice() + 1) + " \ntotal: 0"); // change the label of the house's total
            imageView.setImage(defaultHouseImage); // set house image to the default one
        }
    }

    public void gameEnd(boolean won) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("endStage.fxml"));
        Stage endStage = new Stage();
        if(won){
            endStage.setTitle("YOU WIN!"); //create a window when you win
        } else{
            endStage.setTitle("YOU LOSE!"); //create an other window when you lose
            game.getPlayer().setPoints(0);
            playerPoints.setText("Player points: 0");
        }
        endStage.setScene(new Scene(root, 450, 350)); // window is on top of the main game window
        endStage.show();
        endStage.setAlwaysOnTop(true);
    }

    public void resetGame() {

        game.reset();
        house1Image.setImage(defaultHouseImage);
        house2Image.setImage(defaultHouseImage);
        house3Image.setImage(defaultHouseImage);
        house4Image.setImage(defaultHouseImage);
        disableImages(false);
        cardsLeft.setText("Cards left: 40");
        house1Total.setText("House \ntotal: 0");
        house2Total.setText("House \ntotal: 0");
        house3Total.setText("House \ntotal: 0");
        house4Total.setText("House \ntotal: 0");
        playerPoints.setText("Player points: 0");

    }
    public void quitGame(ActionEvent actionEvent){
        System.exit(0);
    }

    public void disableImages(boolean value) {
        house1Image.setDisable(value);
        house2Image.setDisable(value);
        house3Image.setDisable(value);
        house4Image.setDisable(value);
    }

    public static Game getGame() {
        return game;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showCurrentCard();
        vbox.setBackground(new Background(image));
    }
}
