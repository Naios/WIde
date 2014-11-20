package wide.modules.gui.core.view.login;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import wide.core.Constants;
import wide.core.WIde;

public class LoginForm
{
	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private GridPane mainPane;

	@FXML
	private CheckBox autologinCheckbox;

	@FXML
	private CheckBox savePasswordCheckbox;

	@FXML
	private TextField field_chardb;

	@FXML
	private TextField field_host;

	@FXML
	private PasswordField field_password;

	@FXML
	private TextField field_port;

	@FXML
	private TextField field_user;

	@FXML
	private TextField field_worlddb;

	@FXML
	private Label errorLabel;

	@FXML
	void loginButtonPressed(ActionEvent event)
	{
		tryConnect();
	}

	@FXML
	void initialize()
	{
		assert autologinCheckbox != null : "fx:id=\"autologinCheckbox\" was not injected: check your FXML file 'LoginForm.fxml'.";
		assert field_chardb != null : "fx:id=\"field_chardb\" was not injected: check your FXML file 'LoginForm.fxml'.";
		assert field_host != null : "fx:id=\"field_host\" was not injected: check your FXML file 'LoginForm.fxml'.";
		assert field_password != null : "fx:id=\"field_password\" was not injected: check your FXML file 'LoginForm.fxml'.";
		assert field_port != null : "fx:id=\"field_port\" was not injected: check your FXML file 'LoginForm.fxml'.";
		assert field_user != null : "fx:id=\"field_user\" was not injected: check your FXML file 'LoginForm.fxml'.";
		assert field_worlddb != null : "fx:id=\"field_worlddb\" was not injected: check your FXML file 'LoginForm.fxml'.";
		assert mainPane != null : "fx:id=\"pane\" was not injected: check your FXML file 'LoginForm.fxml'.";

		field_chardb.textProperty().bindBidirectional(
				WIde.getConfig().getProperty(Constants.PROPERTY_DATABASE_CHARACTER.get()));

		field_host.textProperty().bindBidirectional(WIde.getConfig().getProperty(Constants.PROPERTY_DATABASE_HOST.get()));

		field_port.textProperty().bind(WIde.getConfig().getProperty(Constants.PROPERTY_DATABASE_PORT.get()));

		field_user.textProperty().bindBidirectional(WIde.getConfig().getProperty(Constants.PROPERTY_DATABASE_USER.get()));

		field_worlddb.textProperty().bindBidirectional(
		        WIde.getConfig().getProperty(Constants.PROPERTY_DATABASE_WORLD.get()));
		field_password.textProperty().bindBidirectional(
		        WIde.getConfig().getProperty(Constants.PROPERTY_DATABASE_PASSWORD.get()));

		final StringConverter<Boolean> stringToBooleanconverter = new StringConverter<Boolean>()
		{
			@Override
			public Boolean fromString(String from)
			{
				return Boolean.valueOf(from);
			}

			@Override
			public String toString(Boolean to)
			{
				return to.toString();
			}
		};

		autologinCheckbox.selectedProperty().set(
				stringToBooleanconverter.fromString(WIde.getConfig().getProperty(
						"DB:Autologin").get()));
		Bindings.bindBidirectional(WIde.getConfig().getProperty(Constants.PROPERTY_DATABASE_AUTOLOGIN.get()),
				autologinCheckbox.selectedProperty(), stringToBooleanconverter);

		savePasswordCheckbox.selectedProperty().set(
				stringToBooleanconverter.fromString(WIde.getConfig().getProperty(
						"DB:SavePassword").get()));
		Bindings.bindBidirectional(WIde.getConfig().getProperty(Constants.PROPERTY_DATABASE_SAVE_PASSWORD.get()),
				savePasswordCheckbox.selectedProperty(),
				stringToBooleanconverter);

		savePasswordCheckbox.selectedProperty().addListener(
				new ChangeListener<Boolean>()
				{

					final String text = "Note: Your password is store as plain text!";

					@Override
					public void changed(
							ObservableValue<? extends Boolean> arg0,
							Boolean arg1, Boolean val)
					{
						if (val)
							errorLabel.setText(text);
						else if (errorLabel.getText().equals(text))
							errorLabel.setText("");
					}
				});

		if (autologinCheckbox.isSelected())
			tryConnect();
	}

	private void tryConnect()
	{
		errorLabel.setText(String.format("Connecting to %s...",
				WIde.getConfig().getProperty("DB:Host").get()));

	}
}
