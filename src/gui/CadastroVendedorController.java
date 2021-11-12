package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import com.mysql.cj.util.Util;

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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Vendedor;
import model.exceptions.ValidacaoException;
import model.services.VendedorService;

public class CadastroVendedorController implements Initializable {

	private Vendedor entidade;
	private VendedorService service;
	private List<DataChangeListener> DataChangeListeners = new ArrayList<>();
	
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtNome;
	@FXML
	private TextField txtEmail;
	@FXML
	private DatePicker dpDataNasc;
	@FXML
	private TextField txtSalarioBase;
	@FXML
	private Label LabelErroName;
	@FXML
	private Label LabelErroEmail;
	@FXML
	private Label LabelErroDataNasc;
	@FXML
	private Label LabelErroSalarioBase;
	@FXML
	private Button btSalvar;
	@FXML
	private Button btCancelar;

	public void setVendedor(Vendedor entidade) {
		this.entidade = entidade;
	}
	public void subscribeDataChangeListener(DataChangeListener listener) {
		DataChangeListeners.add(listener);
	}
	public void setVendedorService(VendedorService service) {
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
		txtEmail.setText(entidade.getEmail());
		Locale.setDefault(Locale.US);
		txtSalarioBase.setText(String.format("%.2f", entidade.getSalarioBase()));
		txtNome.setText(entidade.getNome());
		if (entidade.getDataNasc()!=null) {
			dpDataNasc.setValue(LocalDate.ofInstant(entidade.getDataNasc().toInstant(),ZoneId.systemDefault()));
		}
		
	}	
	
	private void notifyDataChangeListeners() {
		for(DataChangeListener listener: DataChangeListeners) {
			listener.onDataChanged();
		}
	}
	private Vendedor getDadosFormulario() {
		Vendedor obj = new Vendedor();
		
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
		Constraints.setTextFieldMaxLength(txtNome, 70);
		Constraints.setTextFieldDouble(txtSalarioBase);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Utils.formatDatePicker(dpDataNasc, "dd/MM/yyyy");
		
	}
	private void setMsgErro(Map<String, String> erros) {
		Set<String> campos = erros.keySet();
		
		if (campos.contains("nome")) {
			LabelErroName.setText(erros.get("nome"));
		}
	}

}
