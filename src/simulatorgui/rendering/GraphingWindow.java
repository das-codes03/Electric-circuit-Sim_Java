package simulatorgui.rendering;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

import javax.swing.JPanel;

import utilities.NumericUtilities;

public class GraphingWindow extends JPanel {
	public class GraphingChannel {
		public final Color c;
		private boolean isDrawing;

		public void setDrawing(boolean b) {
			isDrawing = b;
		}

		public GraphingChannel(Color c) {
			this.c = c;
		}

		public class Entry implements Comparable<Entry> {
			private double t;

			public double getT() {
				return t;
			}

			public double getVal() {
				return val;
			}

			private double val;

			public Entry(double t, double val) {
				this.t = t;
				this.val = val;
			}

			@Override
			public int compareTo(Entry o) {
				return Double.compare(t, o.t);
			}
		}

		public double getMin() {
			try {
				return minStack.peek();
			} catch (EmptyStackException e) {
				e.printStackTrace();
			}
			return 0;
		}

		public double getMax() {
			try {
				return maxStack.peek();
			} catch (EmptyStackException e) {
				e.printStackTrace();
			}
			return 0;
		}

		public final SortedSet<Entry> entries = new TreeSet<>(new Comparator<Entry>() {
			@Override
			public int compare(Entry o1, Entry o2) {
				return -o1.compareTo(o2);
			}
		});

		public void addNewEntry(double val, double t) {
			entries.add(new Entry(t, val));
			if (minStack.isEmpty() && maxStack.isEmpty()) {
				minStack.push(val);
				maxStack.push(val);
			}
			if (val <= minStack.peek()) {
				minStack.push(val);
			} else {
				minStack.push(minStack.peek());
			}

			if (val > maxStack.peek()) {
				maxStack.push(val);
			} else {
				maxStack.push(maxStack.peek());
			}
			repaint();
		}

		public void clipOldest() {
			if (entries.isEmpty())
				throw new RuntimeException("Channel is already empty");
			entries.remove(entries.last());
			minStack.pop();
			maxStack.pop();
		}

		private Stack<Double> minStack = new Stack<>();
		private Stack<Double> maxStack = new Stack<>();
	}

	public GraphingWindow() {

	}

	public Map<Long, GraphingChannel> channels = new HashMap<>();
	private double timeRange = 1; // Time range = 1 => entire width of graph = 1 sec
	public double currT = 0;

	double[] getPlotRatio(double val, double t, double currT, double rangeT, double minVal, double maxVal) {
		double tRatio = 1 + (-currT + t) / rangeT;
		double vRange = NumericUtilities.clamp(maxVal - minVal, 1e-10, 1e30);
		double vRatio = (val - minVal) / vRange;
		return new double[] { tRatio, vRatio };
	}

	public void plotLine(Graphics2D gx, double prevVal, double prevT, double currVal, double currT, double timeOffset,
			double min, double max) {
		var prevPlotRatio = getPlotRatio(prevVal, prevT, timeOffset, timeRange, min, max);
		var currPlotRatio = getPlotRatio(currVal, currT, timeOffset, timeRange, min, max);
		int x1 = (int) (prevPlotRatio[0] * getWidth());
		int y1 = (int) (prevPlotRatio[1] * getHeight());
		int x2 = (int) (currPlotRatio[0] * getWidth());
		int y2 = (int) (currPlotRatio[1] * getHeight());
//		gx.setColor(Color.red);
		gx.drawLine(x1, y1, x2, y2);
	}

	public long addChannel(Color c) {
		// iterate through all channels to get max
		long max = Long.MIN_VALUE;
		for (var ch : channels.keySet()) {
			max = Math.max(max, ch);
		}
		channels.put(max + 1, new GraphingChannel(c));
		return max + 1;
	}

	public void addEntry(double val, double t, long ID) {
		channels.get(ID).addNewEntry(val, t);
	}

	@Override
	public void paintComponent(Graphics g) {
		currT += 0;
		BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D renderContext = img.createGraphics();
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		for (var ch : channels.values()) {
			min = Math.min(ch.getMin(), min);
			max = Math.max(ch.getMax(), max);
		}
		for (var ch : channels.values()) {
			Color c = new Color(0, 255, 0);
			renderContext.setColor(c);
			GraphingChannel.Entry prev = null;
			for (var e : ch.entries) {
				synchronized (e) {
					if (e.t < currT - timeRange)
						break;
					if (e.t > currT || prev == null) {
						prev = e;
						continue;
					}
//					renderContext.setColor(new Color((int) (Math.random() * 200), (int) (Math.random() * 200),
//							(int) (Math.random() * 200)));
					plotLine(renderContext, prev.val, prev.t, e.val, e.t, currT, min, max);
					prev = e;
				}
			}
		}
		g.drawImage(img, 0, 0, null);
		renderContext.dispose();
	}
}
