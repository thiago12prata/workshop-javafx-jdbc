package gui;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listener.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Vendedor;
import model.services.VendedorService;

public class ListarVendedoresController implements Initializable, DataChangeListener {

	private VendedorService service;
	@FXML
	private TableView<Vendedor> tableViewVendedor;
	@FXML
	private TableColumn<Vendedor, Integer> tableCollumnId;
	@FXML
	private TableColumn<Vendedor, String> tableCollumnNome;
	@FXML
	private TableColumn<Vendedor, Vendedor> tableColumnEDIT;
	@FXML
	private TableColumn<Vendedor, Vendedor> tableColumnREMOVER;

	@FXML
	private Button btNovo;
	private ObservableList<Vendedor> obsList;

	@FXML
	public void onBtNovoAction(ActionEvent event) {
		Stage stagePai = Utils.stageAtual(event);
		Vendedor obj = new Vendedor();
		criarJanelaDialogo(obj, stagePai, "/gui/CadastroVendedor.fxml");
	}

	public void setVendedorService(VendedorService service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		inicializarNodes();
	}

	private void inicializarNodes() {
		tableCollumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableCollumnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

		// Ajusta a tabela com a tela
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewVendedor.prefHeightProperty().bind(stage.heightProperty());
	}

	public void atualizarTableView() {
		if (service == null) {
			throw new IllegalStateException("Serviço é igual a nulo");
		}

		List<Vendedor> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewVendedor.setItems(obsList);
		iniciarEditButtons();
		initRemoveButtons();
	}

	private void criarJanelaDialogo(Vendedor obj, Stage stagePai, String nomeCompletoView) {
//		try {
//			FXMLLoader loader = new FXMLLoader(getClass().getResource(nomeCompletoView));
//			Pane pane = loader.load();
//			Stage stageDialogo = new Stage();
//
//			CadastroVendedorsController controller = loader.getController();
//			controller.setVendedor(obj);
//			controller.setVendedorService(new VendedorService());
//			controller.subscribeDataChangeListener(this);
//			controller.atualizarFormulario();
//
//			stageDialogo.setTitle("Digite os dados do Vendedor");
//			stageDialogo.setScene(new Scene(pane));
//			stageDialogo.setResizable(false);
//			stageDialogo.initOwner(stagePai);
//			stageDialogo.initModality(Modality.WINDOW_MODAL);
//			stageDialogo.showAndWait();
//		} catch (IOException e) {
//			Alerts.showAlert("IO Exception", "Erro ao Carregar a tela", e.getMessage(), AlertType.ERROR);
//		}
	}

	@Override
	public void onDataChanged() {
		atualizarTableView();

	}

	private void iniciarEditButtons() {

		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Vendedor, Vendedor>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Vendedor obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> criarJanelaDialogo(obj, Utils.stageAtual(event), "/gui/CadastroVendedor.fxml"));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVER.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVER.setCellFactory(param -> new TableCell<Vendedor, Vendedor>() {
			private final Button button = new Button("remover");

			@Override
			protected void updateItem(Vendedor obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removerEntity(obj));
			}
		});
	}

	private void removerEntity(Vendedor obj) {
		Optional<ButtonType> resultado = Alerts.showConfirmation("Confirmação", "Tem certeza que quer deletar?");
		if (resultado.get()==ButtonType.OK) {
			if (service==null) {
				throw new IllegalStateException("Service é nulo");
			}
			try {
				service.remover(obj);
				atualizarTableView();
			} 
			catch (DbIntegrityException e) {
				Alerts.showAlert("Erro ao remover objeto", null, e.getMessage(), AlertType.ERROR);
			}
			
		}
	}

}
