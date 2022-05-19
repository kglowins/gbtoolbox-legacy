package com.github.kglowins.gbtoolbox.kindergarten.ui;


import static com.github.kglowins.gbtoolbox.kindergarten.io.dream3d.Dream3DFile.CRYSTAL_STRUCTURE_POINT_GROUP_MAP;
import static com.github.kglowins.gbtoolbox.kindergarten.io.dream3d.Dream3DFile.pointGroupFromCrystalStructure;
import static com.github.kglowins.gbtoolbox.kindergarten.io.dream3d.Dream3DToGbdatImporter.STATUS_MESSAGE_PROPERTY;
import static com.github.kglowins.gbtoolbox.kindergarten.io.filechooser.FileExtensionUtils.decorateWithExtension;
import static com.github.kglowins.gbtoolbox.kindergarten.ui.bricks.ButtonFactory.createCancelButton;
import static com.github.kglowins.gbtoolbox.kindergarten.ui.bricks.ButtonFactory.createFolderButton;
import static com.github.kglowins.gbtoolbox.kindergarten.ui.bricks.ButtonFactory.createStartButton;

import com.github.kglowins.gbtoolbox.kindergarten.io.dream3d.Dream3DDataSetPaths;
import com.github.kglowins.gbtoolbox.kindergarten.io.dream3d.Dream3DFile;
import com.github.kglowins.gbtoolbox.kindergarten.io.dream3d.Dream3DImportSettings;
import com.github.kglowins.gbtoolbox.kindergarten.io.dream3d.Dream3DToGbdatImporter;
import io.jhdf.api.Group;
import io.jhdf.api.Node;
import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.tuple.Pair;

@Slf4j
public class Dream3DImportPanel extends JPanel {

	private static void recursivePrintGroup(Group group, Set<String> dataSetPaths) {
		for (Node node : group) {
			dataSetPaths.add(node.getPath());
			if (node instanceof Group) {
				recursivePrintGroup((Group) node, dataSetPaths);
			}
		}
	}

	private JLabel outputFileLabel;
	private JTextField outputFileTextField;
	private JButton outputFileButton;
	private JLabel dreamFileLabel;
	private JTextField dreamFileTextField;
	private JButton dreamFileButton;
	private GbdatOrDreamFileChooser fileChooser;

	private JLabel phasesLabel;
	private JComboBox<String> phasesComboBox;
	private JLabel surfaceLabel;
	private JComboBox<String> surfaceComboBox;
	private JLabel crystalStructuresLabel;
	private JComboBox<String> crystalStructuresComboBox;
	private JLabel eulerAnglesLabel;
	private JComboBox<String> eulerAnglesComboBox;
	private JLabel normalsLabel;
	private JComboBox<String> normalsComboBox;
	private JLabel faceLabelsLabel;
	private JComboBox<String> faceLabelsComboBox;
	private JLabel areasLabel;
	private JComboBox<String> areasComboBox;
	private JLabel nodeTypesLabel;
	private JComboBox<String> nodeTypesComboBox;
	private JLabel facesNodesLabel;
	private JComboBox<String> facesNodesComboBox;

	private JCheckBox tripleLinesCheckBox;
	private JCheckBox surfaceCheckBox;

	private JCheckBox simplifyCheckBox;
	private JLabel nodeCoordinatesLabel;
	private JComboBox<String> nodeCoordinatesComboBox;
	private JLabel qSlimPathLabel;
	private JTextField qSlimTextField;
	private JButton qSlimButton;
	private JLabel rateLabel;
	private JTextField rateTextField;
	private JLabel lowerLimitLabel;
	private JTextField lowerLimitTextField;

	private JButton startButton;
	private JButton cancelButton;
	private JLabel statusLabel;

	private Dream3DToGbdatImporter importer;

	public Dream3DImportPanel() {
		initializeElements();
		setLayout(new MigLayout("", "[][][]", "[][][][][][][][][][][][][][][][][][][][]"));
		add(dreamFileLabel, "cell 0 0");
		add(dreamFileTextField, "cell 1 0, growx");
		add(dreamFileButton, "cell 2 0");
		add(crystalStructuresLabel, "cell 0 1");
		add(crystalStructuresComboBox, "cell 1 1, growx");
		add(phasesLabel, "cell 0 2");
		add(phasesComboBox, "cell 1 2, growx");
		add(surfaceLabel, "cell 0 3");
		add(surfaceComboBox, "cell 1 3, growx");
		add(eulerAnglesLabel, "cell 0 4");
		add(eulerAnglesComboBox, "cell 1 4, growx");
		add(normalsLabel, "cell 0 5");
		add(normalsComboBox, "cell 1 5, growx");
		add(faceLabelsLabel, "cell 0 6");
		add(faceLabelsComboBox, "cell 1 6, growx");
		add(areasLabel, "cell 0 7");
		add(areasComboBox, "cell 1 7, growx");
		add(nodeTypesLabel, "cell 0 8");
		add(nodeTypesComboBox, "cell 1 8,growx");
		add(facesNodesLabel, "cell 0 9");
		add(facesNodesComboBox, "cell 1 9, growx");
		add(outputFileLabel, "cell 0 10");
		add(outputFileTextField, "cell 1 10, growx");
		add(outputFileButton, "cell 2 10");
		add(surfaceCheckBox, "cell 0 11 2 1");
		add(tripleLinesCheckBox, "cell 0 12 2 1");
		add(simplifyCheckBox, "cell 0 13 2 1");
		add(nodeCoordinatesLabel, "cell 0 14, align right");
		add(nodeCoordinatesComboBox, "cell 1 14, growx");
		add(qSlimPathLabel, "cell 0 15, alignx right");
		add(qSlimTextField, "cell 1 15, growx");
		add(qSlimButton, "cell 2 15");
		add(rateLabel, "cell 0 16, alignx right");
		add(rateTextField, "cell 1 16, alignx left");
		add(lowerLimitLabel, "cell 0 17, alignx right");
		add(lowerLimitTextField, "cell 1 17, alignx left");
		add(startButton, "cell 0 18, gapy 10");
		add(cancelButton, "cell 0 18, gapy 10");
    	add(statusLabel, "cell 0 19 3 1, growx");
	}

	private void initializeElements() {
		fileChooser = new GbdatOrDreamFileChooser();

		dreamFileLabel = new JLabel("Dream3D File:");
		dreamFileTextField = new JTextField();
		dreamFileTextField.setColumns(55);
		dreamFileTextField.getDocument().addDocumentListener(dreamFileListener());
		dreamFileButton = createFolderButton();
		dreamFileButton.addActionListener(createOpenDream3DListener());

		crystalStructuresLabel = new JLabel("Phase Crystal Structures:");
		crystalStructuresComboBox = new JComboBox<>();
		phasesLabel = new JLabel("Grain Phases:");
		phasesComboBox = new JComboBox<>();
		surfaceLabel = new JLabel("Surface Features:");
		surfaceComboBox = new JComboBox<>();
		eulerAnglesLabel = new JLabel("Grain Average Euler Angles:");
		eulerAnglesComboBox = new JComboBox<>();
		normalsLabel = new JLabel("Face Normals:");
		normalsComboBox = new JComboBox<>();
		faceLabelsLabel = new JLabel("Face Labels:");
		faceLabelsComboBox = new JComboBox<>();
		areasLabel = new JLabel("Face Areas:");
		areasComboBox = new JComboBox<>();
		nodeTypesLabel = new JLabel("Node Types:");
		nodeTypesComboBox = new JComboBox<>();
		facesNodesLabel = new JLabel("Faces Nodes:");
		facesNodesComboBox = new JComboBox<>();

		outputFileLabel = new JLabel("Output File:");
		outputFileTextField = new JTextField();
		outputFileTextField.setColumns(55);
		outputFileButton = createFolderButton();
		outputFileButton.addActionListener(createSaveGbdatListener());

		surfaceCheckBox = new JCheckBox("Exclude Boundaries Belonging to Surface Grains");
		tripleLinesCheckBox = new JCheckBox("Exclude Faces Adjacent to Triple Lines");
		simplifyCheckBox = new JCheckBox("Simplify Mesh");
		simplifyCheckBox.addActionListener(createSimplifyListener());

		nodeCoordinatesLabel = new JLabel("Node Coordinates:");
		nodeCoordinatesLabel.setEnabled(false);
		nodeCoordinatesComboBox = new JComboBox<>();
		nodeCoordinatesComboBox.setEnabled(false);
		qSlimPathLabel = new JLabel("QSlim Path:");
		qSlimPathLabel.setEnabled(false);
		qSlimTextField = new JTextField();
		qSlimTextField.setEnabled(false);
		qSlimTextField.setColumns(10);
		qSlimButton = createFolderButton();
		qSlimButton.setEnabled(false);
		rateLabel = new JLabel("Simplification Rate:");
		rateLabel.setEnabled(false);
		rateTextField = new JTextField();
		rateTextField.setEnabled(false);
		rateTextField.setColumns(5);
		lowerLimitLabel = new JLabel("No Less Faces Than:");
		lowerLimitLabel.setEnabled(false);
		lowerLimitTextField = new JTextField();
		lowerLimitTextField.setEnabled(false);
		lowerLimitTextField.setColumns(5);

		startButton = createStartButton();
		startButton.addActionListener(createStartListener());

		cancelButton = createCancelButton();
		cancelButton.setEnabled(false);
		cancelButton.addActionListener(click -> {
			importer.firePropertyChange(STATUS_MESSAGE_PROPERTY, "","Import Cancelled");
			importer.cancel(true);
		});

		statusLabel = new JLabel("");
	}
	
	private DocumentListener dreamFileListener() {
		return new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				tryGuessDataSetPaths();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				tryGuessDataSetPaths();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				tryGuessDataSetPaths();
			}
		};
	}
	
	private void tryGuessDataSetPaths() {
		String dreamFilePath = dreamFileTextField.getText();
		if (!Files.exists(Paths.get(dreamFilePath))) {
			resetComboBoxes();
			return;
		}
		try {
			Dream3DFile dreamFile = Dream3DFile.init(dreamFilePath);
			Set<String> dataSetPaths = dreamFile.getDataSetPaths();
			Dream3DDataSetPaths guessedDataSetPaths = dreamFile.tryGuessDataSetPaths();
			updateComboBoxes(dataSetPaths, guessedDataSetPaths);
		} catch (Exception e) {
			log.error("Could not read {}", dreamFilePath, e);
			resetComboBoxes();
		}
	}
	
	private void resetComboBoxes() {
		DefaultComboBoxModel<String> emptyModel = new DefaultComboBoxModel<>(new String[]{});
		crystalStructuresComboBox.setModel(emptyModel);
		phasesComboBox.setModel(emptyModel);
		surfaceComboBox.setModel(emptyModel);
		eulerAnglesComboBox.setModel(emptyModel);
		normalsComboBox.setModel(emptyModel);
		faceLabelsComboBox.setModel(emptyModel);
		areasComboBox.setModel(emptyModel);
		nodeTypesComboBox.setModel(emptyModel);
		facesNodesComboBox.setModel(emptyModel);
		nodeCoordinatesComboBox.setModel(emptyModel);
	}
	
	private void updateModel(JComboBox<String> comboBox, String[] internalPaths) {
		comboBox.setModel(new DefaultComboBoxModel<>(internalPaths));
	}
	
	private void updateComboBoxes(Set<String> internalPaths, Dream3DDataSetPaths guessedDataSetPaths) {
		String[] options = internalPaths.stream().toArray(String[]::new);
		updateModel(crystalStructuresComboBox, options);
		updateModel(phasesComboBox, options);
		updateModel(surfaceComboBox, options);
		updateModel(eulerAnglesComboBox, options);
		updateModel(normalsComboBox, options);
		updateModel(faceLabelsComboBox, options);
		updateModel(areasComboBox, options);
		updateModel(nodeTypesComboBox, options);
		updateModel(facesNodesComboBox, options);
		updateModel(nodeCoordinatesComboBox, options);
		
		crystalStructuresComboBox.setSelectedItem(guessedDataSetPaths.getPhaseCrystalStructures());
		phasesComboBox.setSelectedItem(guessedDataSetPaths.getGrainPhases());
		surfaceComboBox.setSelectedItem(guessedDataSetPaths.getSurfaceGrains());
		eulerAnglesComboBox.setSelectedItem(guessedDataSetPaths.getGrainEulerAngles());
		normalsComboBox.setSelectedItem(guessedDataSetPaths.getFaceNormals());
		faceLabelsComboBox.setSelectedItem(guessedDataSetPaths.getFaceGrainIds());
		areasComboBox.setSelectedItem(guessedDataSetPaths.getFaceAreas());
		nodeTypesComboBox.setSelectedItem(guessedDataSetPaths.getNodeTypes());
		facesNodesComboBox.setSelectedItem(guessedDataSetPaths.getFaceNodes());
		nodeCoordinatesComboBox.setSelectedItem(guessedDataSetPaths.getNodeCoordinates());
	}

	private static class GbdatOrDreamFileChooser extends JFileChooser {

		public GbdatOrDreamFileChooser() {
			setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		}

		public void setToFilterDream3D() {
			resetChoosableFileFilters();
			addChoosableFileFilter(new FileNameExtensionFilter("*.dream3d", "dream3d"));
		}

		public void setToFilterGbdat() {
			resetChoosableFileFilters();
			setAcceptAllFileFilterUsed(false);
			addChoosableFileFilter(new FileNameExtensionFilter("*.gbdat", "gbdat"));
		}
	}

	private Dream3DDataSetPaths getDataSetPathsFromComboBoxes() {
		return Dream3DDataSetPaths.builder()
				.faceAreas((String) areasComboBox.getSelectedItem())
				.faceGrainIds((String) faceLabelsComboBox.getSelectedItem())
				.faceNormals((String) normalsComboBox.getSelectedItem())
				.grainEulerAngles((String) eulerAnglesComboBox.getSelectedItem())
				.grainPhases((String) phasesComboBox.getSelectedItem())
				.faceNodes((String) facesNodesComboBox.getSelectedItem())
				.nodeTypes((String) nodeTypesComboBox.getSelectedItem())
				.phaseCrystalStructures((String) crystalStructuresComboBox.getSelectedItem())
				.surfaceGrains((String) surfaceComboBox.getSelectedItem())
				.nodeCoordinates((String) nodeCoordinatesComboBox.getSelectedItem())
				.build();
	}

	private Dream3DImportSettings getSettingsFromForm() {
		Dream3DImportSettings settings = Dream3DImportSettings.builder()
				.skipSurfaceGrains(surfaceCheckBox.isSelected())
				.skipTripleLines(tripleLinesCheckBox.isSelected())
				.simplifyMesh(simplifyCheckBox.isSelected())
				.build();
		if (simplifyCheckBox.isSelected()) {
			settings.setFacesLowerLimit(100);
			settings.setSimplificationRate(0.2);
		}
		return settings;
	}

	private String getPointGroupLabel(long[] crystalStructures, int phaseId) {
	  return CRYSTAL_STRUCTURE_POINT_GROUP_MAP.containsKey(crystalStructures[phaseId]) ?
        pointGroupFromCrystalStructure(crystalStructures[phaseId]).name() :
        "Unsupported";
  }

  private Predicate<Pair<?, Boolean>> isSelectedPhase() {
	  return Pair::getRight;
  } //TODO needs new dependency
  
  private void enableStartButton(boolean enabled) {
	  startButton.setEnabled(enabled);
	  cancelButton.setEnabled(!enabled);
  }

	private ActionListener createOpenDream3DListener() {
		return click -> {
			fileChooser.setToFilterDream3D();
			int result = fileChooser.showOpenDialog(this);
			if (result == JFileChooser.APPROVE_OPTION) {
				dreamFileTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
			}
		};
	}

	private ActionListener createSaveGbdatListener() {
		return click -> {
			fileChooser.setToFilterGbdat();
			int result = fileChooser.showSaveDialog(this);
			if (result == JFileChooser.APPROVE_OPTION) {
				outputFileTextField
					.setText(decorateWithExtension(fileChooser.getSelectedFile().getAbsolutePath(), "gbdat"));
			}
		};
	}

	private ActionListener createStartListener() {
		return click -> {
			statusLabel.setText("Import Started");
			enableStartButton(false);
			Dream3DDataSetPaths dataSetPaths = getDataSetPathsFromComboBoxes();
			Dream3DImportSettings importSettings = getSettingsFromForm();

			Dream3DFile dreamFile = Dream3DFile.init(dreamFileTextField.getText());

			String gbdatPath = outputFileTextField.getText();
			try {
				importer = Dream3DToGbdatImporter.from(dreamFile, dataSetPaths).to(gbdatPath, importSettings);

				long[] crystalStructures = importer.getPhaseCrystalStructures();
				int[] numberOfGrainsPerPhase = importer.getNumberOfGrainsPerPhase();
				Object[] options = IntStream.range(0, importer.getNumberOfPhases())
						.mapToObj(phaseId -> String.format("Phase #%d: Point Group %s, %d Grains", phaseId,
								getPointGroupLabel(crystalStructures, phaseId),
								numberOfGrainsPerPhase[phaseId])).toArray(Object[]::new);

				int selectedPhaseId = -1;
				while (selectedPhaseId < 1) {
					statusLabel.setText("Select Point Group");
					String selectedOption = (String) JOptionPane.showInputDialog(this,
							"Only boundaries separating grains of the selected phase will be imported.",
							"Select Phase", JOptionPane.QUESTION_MESSAGE,null, options,
							options.length > 0 ? options[1] : options[0]);
					if (selectedOption == null) {
						statusLabel.setText("Import cancelled");
						enableStartButton(true);
						return;
					} else {
						selectedPhaseId = IntStream.range(0, importer.getNumberOfPhases())
								.mapToObj(phaseId -> Pair.of(phaseId, selectedOption.equals((String) options[phaseId])))
								.filter(isSelectedPhase())
								.map(Pair::getLeft)
								.findFirst().get();
						if (crystalStructures[selectedPhaseId] < 0 || crystalStructures[selectedPhaseId] > 2 ) {
							JOptionPane.showMessageDialog(this,
									"Unsupported point group can't be chosen.", "Error",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				}
				log.debug("Selected Phase Id: {}", selectedPhaseId);
				importSettings.setPhaseId(selectedPhaseId);
				importer.addPropertyChangeListener(change -> {
					if ("statusMessage".equals(change.getPropertyName())) {
						statusLabel.setText((String) change.getNewValue());
					}
					if ("state".equals(change.getPropertyName()) && (SwingWorker.StateValue.DONE
							.equals(change.getNewValue()))) {
						cancelButton.setEnabled(false);
						startButton.setEnabled(true);
					}
				});
				importer.execute();
			} catch (Exception e) {
				log.error("Failed to import Dream3D file {}", e);
				enableStartButton(true);
			}
		};
	}

	private ActionListener createSimplifyListener() {
		return click -> {
			boolean isSelected = simplifyCheckBox.isSelected();
			nodeCoordinatesLabel.setEnabled(isSelected);
			nodeCoordinatesComboBox.setEnabled(isSelected);
			qSlimPathLabel.setEnabled(isSelected);
			qSlimTextField.setEnabled(isSelected);
			qSlimButton.setEnabled(isSelected);
			rateLabel.setEnabled(isSelected);
			rateTextField.setEnabled(isSelected);
			lowerLimitLabel.setEnabled(isSelected);
			lowerLimitTextField.setEnabled(isSelected);
		};
	}
}
