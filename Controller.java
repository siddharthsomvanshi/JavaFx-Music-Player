/*Created By Siddharth Somvanshi
    Started At 24DEC 2017 -- 9:30pm
 */
//packages and imported java classes
package sample;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//---------------------------------------------------------------------------------------------------------------
//Main Controller Class Starts ----------------------------------------------------------------------------------------------------------

public class Controller implements Initializable {

    //Media Player Configration Starts --------------------------------------------------------------------------------------------------
    //Media Player Variable
    @FXML
    Button toggle = new Button();
    @FXML
    Button close = new Button();
    @FXML
    Button hamburger = new Button();
    @FXML
    Button shufflebt = new Button();
    @FXML
    BorderPane rootpane = new BorderPane();
    @FXML
    ImageView albumart = new ImageView();
    @FXML
    ImageView playera = new ImageView();
    @FXML
    ImageView mute = new ImageView();
    @FXML
    Label volumetxt = new Label();
    @FXML
    Label name = new Label();
    @FXML
    Label artistn = new Label();
    @FXML
    Label currenttime = new Label();
    @FXML
    Label totaltime = new Label();
    @FXML
    Slider seek = new Slider();
    @FXML
    Slider volsli = new Slider();
    @FXML
    Button playpause = new Button();
    @FXML
    Button previous = new Button();
    @FXML
    Button next = new Button();
    @FXML
    Button toogle = new Button();
    //backside
    public List<File> selectedFiles = Stream.of(new File[]{}).collect(Collectors.toList());
    public Iterator<File> itr;
    @FXML
    ListView playlist = new ListView();
    @FXML
    public MediaView mv;
    public MediaPlayer mp;
    public Media me;
    public int statevalue = 1;
    public int currentindex;
    public double volume = 1;
    public boolean shuffle = false;
    public boolean listHide = false;
    //Media Player Methods ---------------------------------------------------------------------------------------------------------------

    public void addmusic() {
        try {
            FileChooser fc = new FileChooser();
            fc.setInitialDirectory(new File(System.getProperty("user.home") + System.getProperty("file.separator") + "Music"));
            fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Music Files -- MP3", "*.mp3"));
            selectedFiles = fc.showOpenMultipleDialog(null);
            //creating meadiagroups
            playlist.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    for (int i = 0; i < selectedFiles.size(); i++) {
                        if (selectedFiles.get(i).getName().equals(newValue)) {
                            currentindex = i;
                            loadplayer(selectedFiles.get(i).toURI().toString());
                        }
                    }
                }
            });
            playlist.getItems().clear();
            //checking whether list is empty or not if not then loading items in listview
            if (selectedFiles != null) {
                for (int i = 0; i < selectedFiles.size(); i++) {
                    playlist.getItems().add(selectedFiles.get(i).getName());
                }
            }
            final Group mediagroup = new Group();
            //creating list iterator
            itr = selectedFiles.iterator();
            //check whether shffle is on/off
            //loading player with first file
            loadplayer(selectedFiles.get(0).toURI().toString());
            //adding mediaviews in group
            mediagroup.getChildren().add(mv);
            String currentmedia = me.getSource().toString();
            //here happens the magic of current index
            loop1:
            for (int j = 0; j <= selectedFiles.size(); j++) {
                if (currentmedia.equals(selectedFiles.get(j).toURI().toString())) {
                    currentindex = j;
                    break loop1;
                }
            }
        } catch (Exception e) {
        }
    }

    //---------------------------------------------------------------------------------------------------------------

    public void loadplayer(String path) {
        //default player initializations
        Image img = new Image("sample/res/default.jpg");
        albumart.setImage(img);
        playera.setImage(img);
        name.setText("Unknown");
        artistn.setText("Unknown");
        if (path == null)
            return;
        if (mp != null) {
            //very important to remove pre-media
            mp.stop();
            mp = null;
            me = null;
            //-------
            seek.setValue(0);
            statevalue = 1;
            String pic = "sample/res/pause.png";
            playpause.setStyle("-fx-background-image: url('" + pic + "');");
        }
        me = new Media(path);
        mp = new MediaPlayer(me);
        mp.setOnReady(new Runnable() {
            public void run() {
                currenttime.setText("0.00");
                double time2 = me.getDuration().toSeconds();
                double lasttime = me.getDuration().toMinutes();
                String duration2 = new Double(lasttime).toString();
                String duration = new Double(time2).toString();
                totaltime.setText(duration2.substring(0, 4));
                seek.setMax(time2);
                seek.valueProperty().addListener(new InvalidationListener() {
                    @Override
                    public void invalidated(javafx.beans.Observable observable) {
                        try {
                            mp.seek(Duration.seconds(seek.getValue()));
                            double time1 = mp.getCurrentTime().toMinutes();
                            String duration = new Double(time1).toString();
                            currenttime.setText(duration.substring(0, 4));
                        } catch (Exception e) {
                        }

                    }
                });
            }
        });
        mp.setAutoPlay(true);
        volsli.setMax(100);
        mp.setVolume(volume);
        volsli.setValue(volume * 100);
        if (volume == 1 || volume * 100 == 1) {
            volumetxt.setText("100");
        } else if (volume < 1) {
            volumetxt.setText(String.valueOf(volume).substring(2, 4));
        } else if (volume < 0.1) {
            volumetxt.setText(String.valueOf(volume).substring(2, 3));
        }
        volsli.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                double val = volsli.getValue();
                volume = val / 100;
                mp.setVolume(val / 100);
                InputStream input = getClass().getResourceAsStream("res/nonmute.png");
                Image image = new Image(input);
                mute.setImage(image);
                if (volume == 1 || volume == 1.0) {
                    volumetxt.setText("100");
                } else if (volume > 0.1) {
                    volumetxt.setText(String.valueOf(volume * 100).substring(0, 2));
                } else if (volume < 0.1) {
                    volumetxt.setText(String.valueOf(volume * 100).substring(0, 1));
                }
                if (val == 0.0) {
                    mp.setVolume(0);
                    InputStream inpt = getClass().getResourceAsStream("res/mute.png");
                    Image imag = new Image(inpt);
                    volsli.setValue(0);
                    mute.setImage(imag);
                }
            }
        });
        try {
            me.getMetadata().addListener(new MapChangeListener<String, Object>() {
                @Override
                public void onChanged(Change<? extends String, ? extends Object> ch) {
                    if (ch.wasAdded()) {
                        handleMetadata(ch.getKey(), ch.getValueAdded());
                    }
                }

                public void handleMetadata(String key, Object value) {
                    if (key.equals("title")) {
                        name.setText("Unknown...");
                        String title = value.toString();
                        name.setText(title);
                    }
                    if (key.equals("artist")) {
                        artistn.setText("Unknown Artist");
                        String artist = "Artist: " + value.toString();
                        artistn.setText(artist);
                    }
                    if (key.equals("image")) {
                        albumart.setImage((Image) value);
                        playera.setImage((Image) value);
                    }
                }
            });
        } catch (RuntimeException re) {
            // Handle construction errors
            System.out.println("Caught Exception: " + re.getMessage());
        }
        //this function works when one media finished or stopped playing
        mp.setOnEndOfMedia(new Runnable() {
            //runnable run() method run automatically
            @Override
            public void run() {
                if (shuffle == false) {
                    if (currentindex == 0) {
                        //Plays the subsequent files
                        currentindex = currentindex + 1;
                        loadplayer(selectedFiles.get(currentindex).toURI().toString());

                    } else if (currentindex == selectedFiles.size() - 1) { //this elseif checks whether the file is last or not if disturbed
                        loadplayer(selectedFiles.get(0).toURI().toString());
                        //updating the value
                        currentindex = 0;
                    } else if (selectedFiles.size() == 2 & currentindex == 1) { // this else block move to next media from list if it is disturbed by user and not last file
                        loadplayer(selectedFiles.get(0).toURI().toString());
                        currentindex = 0;
                    } else {
                        String currentmedia = selectedFiles.get(currentindex + 1).toURI().toString();
                        loadplayer(currentmedia);
                        loop1:
                        for (int j = 0; j <= selectedFiles.size(); j++) {
                            if (currentmedia.equals(selectedFiles.get(j).toURI().toString())) {
                                currentindex = j;
                                break loop1;
                            }
                        }
                    }
                } else {
                    Random rand = new Random();
                    loadplayer(selectedFiles.get(rand.nextInt(selectedFiles.size())).toURI().toString());
                }

            }
        });
    }

    //---------------------------------------------------------------------------------------------------------------


    public void playpause() {
        if (statevalue == 1) {
            statevalue = 0;
            mp.pause();
            String pic = "sample/res/play.png";
            playpause.setStyle("-fx-background-image: url('" + pic + "');");
        } else if (statevalue == 0) {
            statevalue = 1;
            mp.play();
            String pic = "sample/res/pause.png";
            playpause.setStyle("-fx-background-image: url('" + pic + "');");
        } else if (statevalue == 3) {
            statevalue = 1;
            String pic = "sample/res/pause.png";
            playpause.setStyle("-fx-background-image: url('" + pic + "');");
        }
    }

    //---------------------------------------------------------------------------------------------------------------


    public void next() {
        if (shuffle == false) {
            //this two things removes the current media from the player
            mp.stop();
            //as requested next song this make song index to next song
            if (currentindex == 0) {
                currentindex = currentindex + 1;
            } else if (currentindex == selectedFiles.size() - 1) {
                currentindex = 0;
            } else {
                currentindex = currentindex + 1;
            }
            //checking whether it is last song or not
            String currentmedia = selectedFiles.get(currentindex).toURI().toString();
            loadplayer(currentmedia);
            loop1:
            for (int j = 0; j <= selectedFiles.size(); j++) {
                if (currentmedia.equals(selectedFiles.get(j).toURI().toString())) {
                    currentindex = j;
                    break loop1;
                }
            }
        } else {
            Random rand = new Random();
            mp.stop();
            loadplayer(selectedFiles.get(rand.nextInt(selectedFiles.size())).toURI().toString());
        }
        //if it is last song move plaer towars the first song and set index value to 0 i.e fist song
    }


    //---------------------------------------------------------------------------------------------------------------


    public void previous() {
        if(shuffle==false){
        //this two things removes the current media from the player
        mp.stop();
        me = null;
        //as previous requested getting the index of previous song
        int previousmedia = currentindex - 1;
        //checking for whether this fisrst song or not if yes then restart fom firts using itreator
        if (previousmedia < 0) {
            itr = selectedFiles.iterator();
            loadplayer(itr.next().toURI().toString());
        }
        //if this is not fist song using previous index load the previous song
        loadplayer(selectedFiles.get((previousmedia)).toURI().toString());
        currentindex = currentindex - 1;
        itr.equals(selectedFiles.get(currentindex).toURI().toString());
    }else{
        Random rand = new Random();
        mp.stop();
        loadplayer(selectedFiles.get(rand.nextInt(selectedFiles.size())).toURI().toString());
    }
}

    public void mute() {
        Double vol = mp.getVolume() * 100;
        if (vol > 0) {
            mp.setVolume(0);
            InputStream input = getClass().getResourceAsStream("res/mute.png");
            Image image = new Image(input);
            volsli.setValue(0);
            mute.setImage(image);

        } else if (vol == 0) {
            mp.setVolume(1);
            volsli.setValue(100);
            Double AfterReEnable = mp.getVolume() * 100;
            InputStream input = getClass().getResourceAsStream("res/nonmute.png");
            Image image = new Image(input);
            mute.setImage(image);
            volumetxt.setText(vol.toString().substring(0, 2).trim());
            if (AfterReEnable == 100 || AfterReEnable == 100.0) {
                volumetxt.setText("100");
            } else if (AfterReEnable > 10) {
                volumetxt.setText(AfterReEnable.toString().substring(0, 2).trim());
            } else if (AfterReEnable < 10) {
                volumetxt.setText(AfterReEnable.toString().substring(0, 1).trim());
            }
        }
    }
    public void close(){
        System.exit(0);
    }
    public void setShuffle(){
        if(shuffle==false) {
            String val = "grey";
            shufflebt.setStyle("-fx-background-color:"+val+";");
            shuffle = true;
        }else{
            String val = "transparent";
            shufflebt.setStyle("-fx-background-color:"+val+";");
            shuffle=false;
        }
    }
    public void hideList(){
        if(listHide==false) {
            toggle.getScene().getWindow().setWidth(297);
            close.setLayoutX(270);
            hamburger.setLayoutX(240);
            toogle.setLayoutX(230);
            String pic = "sample/res/closeDock.png";
            toggle.setStyle("-fx-background-image: url('"+pic+"');");
            listHide = true;
        }else{
            toggle.getScene().getWindow().setWidth(530);
            close.setLayoutX(507);
            hamburger.setLayoutX(477);
            toogle.setLayoutX(423);
            String pic = "sample/res/openDock.png";
            toggle.setStyle("-fx-background-image: url('"+pic+"');");
            listHide=false;
        }



    }
    //---------------------------------------------------------------------------------------------------------------


    //Auto Runner ---------------------------------------------------------------------------------------------------------------


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rootpane.setOnKeyPressed(new javafx.event.EventHandler<javafx.scene.input.KeyEvent>() {
            @Override
            public void handle(javafx.scene.input.KeyEvent event) {
                String code = event.getCode().toString();
                rootpane.requestFocus();
                switch (code) {
                    case "L":
                        addmusic();
                        break;
                    case "ENTER":
                        playpause.fire();
                        break;
                    case "S":
                        playpause.fire();
                        break;
                    case "N":
                        next.fire();
                        break;
                    case "P":
                        previous.fire();
                        break;
                    case "M":
                        mute();
                        break;
                    case "C":
                        close();
                        break;

                }
            }
        });
        BoxBlur blur = new BoxBlur();
        blur.setHeight(10);
        blur.setWidth(10);
        blur.setIterations(1000);
        playera.setEffect(blur);
    }


}
