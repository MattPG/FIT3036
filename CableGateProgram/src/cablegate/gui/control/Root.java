package cablegate.gui.control;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import javax.annotation.PostConstruct;

import org.datafx.controller.FXMLController;
import org.datafx.controller.flow.Flow;
import org.datafx.controller.flow.FlowException;
import org.datafx.controller.flow.FlowHandler;
import org.datafx.controller.flow.container.AnimatedFlowContainer;
import org.datafx.controller.flow.container.ContainerAnimations;

@FXMLController("root.fxml")
public class Root {

	 
    @FXML
    private StackPane centerPane;
    private FlowHandler flowHandler;
 
    @PostConstruct
    public void init() throws FlowException {
        Flow flow = new Flow(GraphMainView.class);
                //withLink(WizardView1Controller.class, "next", WizardView2Controller.class).
 
		flowHandler = flow.createHandler();
        centerPane.getChildren().add(flowHandler.start(new AnimatedFlowContainer(Duration.millis(320), ContainerAnimations.ZOOM_IN)));
 
        //backButton.setDisable(true);
    }

}
