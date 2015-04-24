package net.ohoooo.powermeter;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ChartCanvas extends Canvas{
	final int datacount = 1000;
	float data[];
	boolean mark[];
	int index = 0;

	float MAX = 10;
	float MIN = 0;
	
	Color backColor = Color.WHITE;
	Color chartColor = Color.BLACK;
	Color markColor = Color.LIGHT_GRAY;

	BufferedImage buffer;
	Graphics2D g;
	
	public ChartCanvas() {
		super();
		
		initData();
		
		this.setSize(1000, 800);
		this.setBackground(backColor);
		
	}
	
	private void initData(){
		data = new float[datacount];
		mark = new boolean[datacount];
		
		for(int i = 0;i < datacount;i++){
			data[i] = 0.0f;
			mark[i] = false;
		}
		buffer = new BufferedImage(1000, 800, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D)buffer.getGraphics();
		g.setColor(backColor);
		g.fillRect(0, 0, 1000, 800);
		
	}

	@Override
	public void paint(Graphics arg0) {
		// TODO Auto-generated method stub
		super.paint(arg0);
		
		Graphics2D g2 = (Graphics2D)arg0;
		g2.drawImage(buffer, 0, 0, null);
	}

	@Override
	public void update(Graphics arg0) {
		// TODO Auto-generated method stub
		super.update(arg0);
		paint(arg0);
	}

	public void setData(float value1,boolean mark1){
		data[index] = value1;
		mark[index] = mark1;
		g.setColor(backColor);
		g.drawLine(index, 0, index, 800);
		if(mark1){
			g.setColor(markColor);
			g.drawLine(index, 0, index, 800);
		}
		g.setColor(chartColor);
		g.drawLine(index, scale(value1), index, scale(value1)+1);
		
		index++;
		
		if(index >= datacount){
			index = 0;
		}
	}
	
	private int scale(float v){
		return 800 - (int)((v - MIN)*800/(MAX-MIN));
	}
}
