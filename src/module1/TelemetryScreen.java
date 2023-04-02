package module1;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import javax.imageio.plugins.tiff.ExifParentTIFFTagSet;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

public class TelemetryScreen extends JPanel{
	public static enum runStatus {
		RUNNING, PAUSED, STOPPED
	}

	private static HashMap<Integer, String> unitMap = new HashMap<>(); // Map<x,suffix> which means 10^x -> prefix
	static {
		unitMap.put(12, "T");
		unitMap.put(9, "G");
		unitMap.put(6, "M");
		unitMap.put(3, "k");
		unitMap.put(0, "");
		unitMap.put(-3, "m");
		unitMap.put(-6, "μ");
		unitMap.put(-9, "n");
		unitMap.put(-12, "p");
	}
	private JLabel timeElapsedLabel;
	private JLabel livePowerLabel;
	private JLabel totalEnergyLabel;
	private JLabel livePeakCurrentLabel;
	private JLabel peakCurrentLabel;
	private JLabel StatusLabel;
	private JComponent parent;

	String getPrefixed(double val) {
		String str = "";
		int x = 0;
		if(val > 0)
			x = 3 * ((int) (Math.log10(val) / 3));
		val /= Math.pow(10, x);
		str = String.format("%.8f", val) + " " + unitMap.get(x);
		return str;
	}

	public TelemetryScreen(JComponent parent) {
		this.setBackground(new Color(20, 20, 20));
		parent.add(this);
		this.setLayout(new FormLayout(
				new ColumnSpec[] { FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC, },
				new RowSpec[] { FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, }));

		JLabel lbl_Simulator_status = new JLabel("Simulator status");
		lbl_Simulator_status.setForeground(Color.YELLOW);
		this.add(lbl_Simulator_status, "2, 2");

		StatusLabel = new JLabel("PAUSED");
		StatusLabel.setForeground(new Color(0, 255, 255));
		StatusLabel.setFont(new Font("Consolas", Font.BOLD, 15));
		this.add(StatusLabel, "2, 4");

		JLabel lbl_Peak_recorded_current = new JLabel("Peak recorded current");
		lbl_Peak_recorded_current.setForeground(new Color(255, 255, 0));
		this.add(lbl_Peak_recorded_current, "2, 6");

		peakCurrentLabel = new JLabel("0.000000 A");
		peakCurrentLabel.setForeground(new Color(0, 255, 0));
		peakCurrentLabel.setFont(new Font("Consolas", Font.BOLD, 20));
		this.add(peakCurrentLabel, "2, 8");

		JLabel lbl_Live_peak_current = new JLabel("Live peak current");
		lbl_Live_peak_current.setForeground(Color.YELLOW);
		this.add(lbl_Live_peak_current, "2, 10");

		livePeakCurrentLabel = new JLabel("0.000000 A");
		livePeakCurrentLabel.setForeground(Color.GREEN);
		livePeakCurrentLabel.setFont(new Font("Consolas", Font.BOLD, 20));
		this.add(livePeakCurrentLabel, "2, 12");

		JLabel lbl_Live_power_consumption = new JLabel("Live power consumption");
		lbl_Live_power_consumption.setForeground(Color.YELLOW);
		this.add(lbl_Live_power_consumption, "2, 14");

		livePowerLabel = new JLabel("0.000000 W");
		livePowerLabel.setForeground(Color.GREEN);
		livePowerLabel.setFont(new Font("Consolas", Font.BOLD, 20));
		this.add(livePowerLabel, "2, 16");

		JLabel lbl_Total_energy_consumed = new JLabel("Total energy consumed");
		lbl_Total_energy_consumed.setForeground(Color.YELLOW);
		this.add(lbl_Total_energy_consumed, "2, 18");

		totalEnergyLabel = new JLabel("0.000000 kWh");
		totalEnergyLabel.setForeground(Color.GREEN);
		totalEnergyLabel.setFont(new Font("Consolas", Font.BOLD, 20));
		this.add(totalEnergyLabel, "2, 20");

		JLabel lbl_Simulation_time_elapsed = new JLabel("Simulation time elapsed");
		lbl_Simulation_time_elapsed.setForeground(Color.YELLOW);
		this.add(lbl_Simulation_time_elapsed, "2, 22");
		timeElapsedLabel = new JLabel("0.000000 s");
		timeElapsedLabel.setForeground(new Color(255, 255, 255));
		timeElapsedLabel.setFont(new Font("Consolas", Font.BOLD, 15));
		this.add(timeElapsedLabel, "2, 24");
	}

	public void updateValues(double t, double livePower, double totalEnergy, double livePeakCurrent, double peakCurrent,
			runStatus status) {

		if (t < 60) {
			timeElapsedLabel.setText(getPrefixed(t) + "s");
		} else if (t < 60 * 60) {
			String tStr = String.format("%02d:%02.3f s", (int) (t / 60), t % 60);
			timeElapsedLabel.setText(tStr);
		} else {
			String tStr = String.format("%02d:%02d:%02.1f s", (int) (t / 60 / 60), (int) ((t % (60 * 60)) / 60), t % 60);
			timeElapsedLabel.setText(tStr);
		}

		livePowerLabel.setText(getPrefixed(livePower) + "W");
		totalEnergyLabel.setText(getPrefixed(totalEnergy) + "Wh");
		livePeakCurrentLabel.setText(getPrefixed(livePeakCurrent) + "A");
		peakCurrentLabel.setText(getPrefixed(peakCurrent) + "A");
		switch (status) {
		case PAUSED: {
			StatusLabel.setText("PAUSED");
			StatusLabel.setForeground(Color.CYAN);
			break;
		}
		case RUNNING: {
			StatusLabel.setText("RUNNING");
			StatusLabel.setForeground(Color.GREEN);
			break;
		}
		case STOPPED: {
			StatusLabel.setText("TERMINATED");
			StatusLabel.setForeground(Color.RED);
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + status);
		}
		repaint();
	}
}
