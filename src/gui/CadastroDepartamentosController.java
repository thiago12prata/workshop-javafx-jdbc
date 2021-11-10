package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

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
import model.exceptions.ValidacaoException;
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
		if (entidade==null) {
			throw new IllegalStateException("Entida é nula");
		}
		if (service==null) {
			throw new IllegalStateException("Service não foi injetado");
		}
		try {
			entidade = getDadosFormulario();
			service.salvarOuAtualizar(entidade);
			Utils.stageAtual(event).close();
			notifyDataChangeListeners();
		} 
		catch (ValidacaoException e) {
			setMsgErro(e.getErros());
		}
		catch (DbException e) {
			Alerts.showAlert("Erro ao Salvar objeto", null, e.getMessage(), AlertType.ERROR);
		}
	}
	@FXML
	public void onBtCancelarAction(ActionEvent event){
		Utils.stageAtual(event).close();
	}
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		inicializarNodes();
	}
	public void atualizarFormulario() {
		if (entidade==null) {
			throw new IllegalStateException("Entidade é nula");
		}
		txtId.setText(String.valueOf(entidade.getId()));
		txtNome.setText(entidade.getNome());
	}	
	
	private void notifyDataChangeListeners() {
		for(DataChangeListener listener: DataChangeListeners) {
			listener.onDataChanged();
		}
	}
	private Departamento getDadosFormulario() {
		Departamento obj = new Departamento();
		
		ValidacaoException exception = new ValidacaoException("Erro de Validação");
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		if(txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			exception.addErro("nome", "O campo não pode ser vazio");
		}
		obj.setNome(txtNome.getText());
		
		if (exception.getErros().size() > 0) {
			throw exception;
		}
		return obj;
	}
	private void inicializarNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtNome, 30);
	}
	private void setMsgErro(Map<String, String> erros) {
		Set<String> campos = erros.keySet();
		
		if (campos.contains("nome")) {
			LabelErroName.setText(erros.get("nome"));
		}
	}

}
