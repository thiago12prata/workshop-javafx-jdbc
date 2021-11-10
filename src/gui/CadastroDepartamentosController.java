package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import db.DbException;
import gui.listener.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Departamento;
import model.services.DepartamentoService;

public class CadastroDepartamentosController implements Initializable {

	private Departamento entidade;
	private DepartamentoService service;
	private List<DataChangeListener> DataChangeListeners = new ArrayList<>();
	
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtNome;
	@FXML
	private Label LabelErroName;
	@FXML
	private Button btSalvar;
	@FXML
	private Button btCancelar;

	public void setDepartamento(Departamento entidade) {
		this.entidade = entidade;
	}
	public void subscribeDataChangeListener(DataChangeListener listener) {
		DataChangeListeners.add(listener);
	}
	public void setDepartamentoService(DepartamentoService service) {
		this.service = service;
	}
	
	@FXML
	public void onBtSalvarAction(ActionEvent event){
		entidade = getDadosFormulario();
		if (entidade==null) {
			throw new IllegalStateException("Entida é nula");
		}
		if (service==null) {
			throw new IllegalStateException("Service não foi injetado");
		}
		try {
			service.salvarOuAtualizar(entidade);
			Utils.stageAtual(event).close();
			notifyDataChangeListeners();
		} catch (DbException e) {
			Alerts.showAlert("Erro ao Salvar objeto", null, e.getMessage(), AlertType.ERROR);
		}
	}
	private void notifyDataChangeListeners() {
		for(DataChangeListener listener: DataChangeListeners) {
			listener.onDataChanged();
		}
	}
	private Departamento getDadosFormulario() {
		Departamento obj = new Departamento();
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		obj.setNome(txtNome.getText());
		return obj;
	}

	@FXML
	public void onBtCancelarAction(ActionEvent event){
		Utils.stageAtual(event).close();
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		inicializarNodes();
	}
	private void inicializarNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtNome, 30);
	}
	public void atualizarFormulario() {
		if (entidade==null) {
			throw new IllegalStateException("Entidade é nula");
		}
		txtId.setText(String.valueOf(entidade.getId()));
		txtNome.setText(entidade.getNome());
	}
}
