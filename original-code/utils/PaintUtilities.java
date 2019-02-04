package utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.apache.commons.math3.util.FastMath;


public class PaintUtilities {
	
	
	
	public static final void drawACurve(final Graphics2D g, final Point2D[] pts) {
		
		final Path2D curve = new Path2D.Double();
		
		int index = 0;
		
		final int N = pts.length;
		
		if(N > 0) {
		
			curve.moveTo(pts[index].getX(), pts[index].getY());
		
			while(index + 3 < N) {
			
				curve.curveTo(pts[index + 1].getX(), pts[index + 1].getY(),
						pts[index + 2].getX(), pts[index + 2].getY(),
						pts[index + 3].getX(), pts[index + 3].getY());
				index += 3;
			}
		
			if(index == N - 2) curve.lineTo(pts[index + 1].getX(), pts[index + 1].getY());
			else if(index == N - 3) curve.quadTo(pts[index + 1].getX(), pts[index + 1].getY(),
				  							 pts[index + 2].getX(), pts[index + 2].getY());
		
			g.draw(curve);
		}
	}
	
	
	
	public static final void drawZone(final Graphics2D g, ArrayList<Point2D> pts) {
		
		final Path2D curve = new Path2D.Double();
		
		int index = 0;
		
		final int N = pts.size();
		
		if(N > 0) {

		
			curve.moveTo(pts.get(index).getX(), pts.get(index).getY());
		
			while(index + 3 < N) {
			
				curve.curveTo(pts.get(index+1).getX(), pts.get(index+1).getY(),
						pts.get(index+2).getX(), pts.get(index+2).getY(),
						pts.get(index+3).getX(), pts.get(index+3).getY());
				index += 3;
			}
		
			if(index == N - 2) curve.lineTo(pts.get(index+1).getX(), pts.get(index+1).getY());
			else if(index == N - 3) curve.quadTo(pts.get(index+1).getX(), pts.get(index+1).getY(),
					pts.get(index+2).getX(), pts.get(index+2).getY());
		
			g.draw(curve);
		}
	}
	
	
	public static final void drawPoleFigureHex(final Graphics2D g,
			  final int gapx, final int gapy, final int radius, boolean bold) {


		Ellipse2D circle = new Ellipse2D.Double(gapx, gapy, 2*radius, 2*radius);

		if(bold) {
			g.setColor(Color.BLACK);
			final float dashArr[] = {5.0f, 7.5f};
			final BasicStroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 12.5f, dashArr, 0.0f);
			g.setStroke(dashed);
		} else {
			g.setColor(Color.DARK_GRAY);
			final float dashArr[] = {5.0f, 7.5f};
			final BasicStroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 12.5f, dashArr, 0.0f);
			g.setStroke(dashed);			
		}

		g.draw(circle);

		Line2D axis1 = new Line2D.Double(gapx, gapy + radius, gapx + 2*radius, gapy + radius);
		Line2D axis2 = new Line2D.Double(gapx+ radius, gapy, gapx + radius, gapy + 2*radius);

		Line2D axis3 = new Line2D.Double(gapx + radius + radius*Math.cos(Math.PI / 6d),
										 gapx + radius + radius*Math.sin(Math.PI / 6d),
										 gapx + radius + radius*Math.cos(Math.PI + Math.PI / 6d),
										 gapx + radius + radius*Math.sin(Math.PI + Math.PI / 6d));
		

		Line2D axis4 = new Line2D.Double(gapx + radius + radius*Math.cos(2d * Math.PI / 6d),
				 gapx + radius + radius*Math.sin(2d * Math.PI / 6d),
				 gapx + radius + radius*Math.cos(Math.PI + 2d * Math.PI / 6d),
				 gapx + radius + radius*Math.sin(Math.PI + 2d * Math.PI / 6d));
		
		Line2D axis5 = new Line2D.Double(gapx + radius + radius*Math.cos(5d * Math.PI / 6d),
				 gapx + radius + radius*Math.sin(5d * Math.PI / 6d),
				 gapx + radius + radius*Math.cos(Math.PI + 5d * Math.PI / 6d),
				 gapx + radius + radius*Math.sin(Math.PI + 5d * Math.PI / 6d));
		
		Line2D axis6 = new Line2D.Double(gapx + radius + radius*Math.cos(4d * Math.PI / 6d),
				 gapx + radius + radius*Math.sin(4d * Math.PI / 6d),
				 gapx + radius + radius*Math.cos(Math.PI + 4d * Math.PI / 6d),
				 gapx + radius + radius*Math.sin(Math.PI + 4d * Math.PI / 6d));

		
		
		if(!bold) {
			final float dashArr[] = {5.0f, 7.5f};
			final BasicStroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 12.5f, dashArr, 0.0f);
			g.setStroke(dashed);
		}
		
		g.draw(axis1);
		g.draw(axis2);
		g.draw(axis3);
		g.draw(axis4);
		g.draw(axis5);
		g.draw(axis6);
			
	}
	
	
	public static final void drawPoleFigureTetr(final Graphics2D g,
			  final int gapx, final int gapy, final int radius, boolean bold) {


		Ellipse2D circle = new Ellipse2D.Double(gapx, gapy, 2*radius, 2*radius);

		if(bold) {
			g.setColor(Color.BLACK);
			final float dashArr[] = {5.0f, 7.5f};
			final BasicStroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 12.5f, dashArr, 0.0f);
			g.setStroke(dashed);
		} else {
			g.setColor(Color.DARK_GRAY);
			final float dashArr[] = {5.0f, 7.5f};
			final BasicStroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 12.5f, dashArr, 0.0f);
			g.setStroke(dashed);
		}

		g.draw(circle);

		Line2D axis1 = new Line2D.Double(gapx, gapy + radius, gapx + 2*radius, gapy + radius);
		Line2D axis2 = new Line2D.Double(gapx+ radius, gapy, gapx + radius, gapy + 2*radius);

		Line2D axis3 = new Line2D.Double(gapx + radius + radius*Math.cos(Math.PI / 4d),
										 gapx + radius + radius*Math.sin(Math.PI / 4d),
										 gapx + radius + radius*Math.cos(Math.PI + Math.PI / 4d),
										 gapx + radius + radius*Math.sin(Math.PI + Math.PI / 4d));
		

		Line2D axis4 = new Line2D.Double(gapx + radius + radius*Math.cos(3d*Math.PI / 4d),
				 gapx + radius + radius*Math.sin(3d*Math.PI / 4d),
				 gapx + radius + radius*Math.cos(Math.PI + 3d*Math.PI / 4d),
				 gapx + radius + radius*Math.sin(Math.PI + 3d*Math.PI / 4d));
		
	
		if(!bold) {
			final float dashArr[] = {5.0f, 7.5f};
			final BasicStroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 12.5f, dashArr, 0.0f);

			g.setStroke(dashed);
		}
		
		g.draw(axis1);
		g.draw(axis2);
		g.draw(axis3);
		g.draw(axis4);

			
	}
	
	
	public static final void drawPoleFigureOrth(final Graphics2D g,
			  final int gapx, final int gapy, final int radius, boolean bold) {


		Ellipse2D circle = new Ellipse2D.Double(gapx, gapy, 2*radius, 2*radius);

		if(bold) {
			g.setColor(Color.BLACK);
			final float dashArr[] = {5.0f, 7.5f};
			final BasicStroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 12.5f, dashArr, 0.0f);
			g.setStroke(dashed);
		} else {
			
			g.setColor(Color.DARK_GRAY);
			final float dashArr[] = {5.0f, 7.5f};
			final BasicStroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 12.5f, dashArr, 0.0f);
			g.setStroke(dashed);
		}

		g.draw(circle);

		Line2D axis1 = new Line2D.Double(gapx, gapy + radius, gapx + 2*radius, gapy + radius);
		Line2D axis2 = new Line2D.Double(gapx+ radius, gapy, gapx + radius, gapy + 2*radius);

	
	
		if(!bold) {
			final float dashArr[] = {5.0f, 7.5f};
			final BasicStroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 12.5f, dashArr, 0.0f);

			g.setStroke(dashed);
		}
		g.draw(axis1);
		g.draw(axis2);
		

			
	}
	
	public static final void drawPoleFigureAxes(final Graphics2D g,
										  final int gapx, final int gapy, final int radius, boolean bold) {
		
		final int ISOSAMPLES = 64;
		
		Ellipse2D circle = new Ellipse2D.Double(gapx, gapy, 2*radius, 2*radius);
				
		if(bold) {
			g.setColor(Color.BLACK);
			final float dashArr[] = {5.0f, 7.5f};
			final BasicStroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 12.5f, dashArr, 0.0f);
			g.setStroke(dashed);
		} else {
			g.setColor(Color.DARK_GRAY);
			final float dashArr[] = {5.0f, 7.5f};
			final BasicStroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 12.5f, dashArr, 0.0f);
			g.setStroke(dashed);
		}
				
		g.draw(circle);
				
		Line2D axis1 = new Line2D.Double(gapx, gapy + radius, gapx + 2*radius, gapy + radius);
		Line2D axis2 = new Line2D.Double(gapx+ radius, gapy, gapx + radius, gapy + 2*radius);
				
		Line2D axis3 = new Line2D.Double(gapx + (int)Math.round(radius*(1d - 0.5d*Math.sqrt(2d) )),
										 gapy + (int)Math.round(radius*(1d - 0.5d*Math.sqrt(2d) )),
										 gapx + radius + (int)Math.round(radius *0.5d *Math.sqrt(2d)),
										 gapy + radius + (int)Math.round(radius *0.5d *Math.sqrt(2d)) );
				
		Line2D axis4 = new Line2D.Double(gapx + (int)Math.round(radius*(1d - 0.5d*Math.sqrt(2d) )),
										 gapy + radius + (int)Math.round(radius *0.5d *Math.sqrt(2d)),
										 gapx + radius + (int)Math.round(radius *0.5d *Math.sqrt(2d)),				 
										 gapy + (int)Math.round(radius*(1d - 0.5d*Math.sqrt(2d) )));

		if(!bold) {
			final float dashArr[] = {5.0f, 7.5f};
			final BasicStroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 12.5f, dashArr, 0.0f);
			    
			g.setStroke(dashed);
		}
		g.draw(axis1);//TODO
		g.draw(axis2);
		g.draw(axis3);
		g.draw(axis4);
				
					
										
		final double[] t = new double[ISOSAMPLES + 1];

		final double dt = Math.PI / ISOSAMPLES;
		for(int i = 0; i <= ISOSAMPLES; ++i) t[i] = -0.5d * Math.PI + i * dt;
				
		Point2D[] pts = new Point2D[ISOSAMPLES + 1];
		for(int i = 0; i <= ISOSAMPLES; ++i) 
			pts[i] = new Point2D.Double(gapx + radius*(1d + (FastMath.tan(FastMath.atan(1d/FastMath.cos(t[i]))*0.5d)*FastMath.cos(t[i]))) ,
										gapy + radius*(1d + (FastMath.tan(FastMath.atan(1d/FastMath.cos(t[i]))*0.5d)*FastMath.sin(t[i]))) );
		drawACurve(g, pts);
		        
		        
		pts = new Point2D[ISOSAMPLES + 1];
		for(int i = 0; i <= ISOSAMPLES; ++i) 
			pts[i] = new Point2D.Double(gapx + radius*(1d - (FastMath.tan(FastMath.atan(1d/FastMath.cos(t[i]))*0.5d)*FastMath.cos(t[i]))) ,
										gapy + radius*(1d + (FastMath.tan(FastMath.atan(1d/FastMath.cos(t[i]))*0.5d)*FastMath.sin(t[i]))) );
		drawACurve(g, pts);
		        
		        
        pts = new Point2D[ISOSAMPLES + 1];
		for(int i = 0; i <= ISOSAMPLES; ++i) 
			pts[i] = new Point2D.Double(gapx + radius*(1d + (FastMath.tan(FastMath.atan(1d/FastMath.cos(t[i]))*0.5d)*FastMath.sin(t[i]))) ,
										gapy + radius*(1d + (FastMath.tan(FastMath.atan(1d/FastMath.cos(t[i]))*0.5d)*FastMath.cos(t[i]))) );
        drawACurve(g, pts);
		        
		        
        pts = new Point2D[ISOSAMPLES + 1];
		for(int i = 0; i <= ISOSAMPLES; ++i) 
			pts[i] = new Point2D.Double(gapx + radius*(1d + (FastMath.tan(FastMath.atan(1d/FastMath.cos(t[i]))*0.5d)*FastMath.sin(t[i]))) ,
										gapy + radius*(1d - (FastMath.tan(FastMath.atan(1d/FastMath.cos(t[i]))*0.5d)*FastMath.cos(t[i]))) );
        drawACurve(g, pts);				
	}
	
	
	
	public static final void drawPoints(final Graphics2D g, final ArrayList<Point2D> pts, Color col, final int size) {
		
		g.setColor(col);
		for(Point2D pt : pts) g.fillOval((int)pt.getX(), (int)pt.getY(), size, size);		
	}
	
	
	public static final void drawCircles(final Graphics2D g, final ArrayList<Point2D> pts, Color col, final int size) {
		
		g.setColor(col);
		g.setStroke(new BasicStroke(3f));//TODO
		for(Point2D pt : pts) g.drawOval((int)pt.getX(), (int)pt.getY(), size, size);	
	}
	
	
	
	public static final void drawSSTCub(final Graphics2D g,
			  final int gapx, final int gapy, final int radius, int width) {

		final int ISOSAMPLES = 32;

		g.setColor(Color.BLACK);
		final BasicStroke dashed = new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
		g.setStroke(dashed);


		Line2D axis1 = new Line2D.Double(gapx + (2*radius-width),
				gapy + 2*radius - (2*radius-width)/2,
				
				gapx + 2*radius,
				gapy + 2*radius - (2*radius-width)/2);
		
		Line2D axis2 = new Line2D.Double(gapx + (2*radius-width),
				gapy + 2*radius - (2*radius-width)/2,
				
				gapx + (2*radius-width) + 0.883663*width,
				gapy + 2*radius - (2*radius-width)/2 -0.883663*width);	
		
		


		g.draw(axis1);
		g.draw(axis2);

			
		final double[] t = new double[ISOSAMPLES + 1];

		final double dt = 0.25d*Math.PI / ISOSAMPLES;
		for(int i = 0; i <= ISOSAMPLES; ++i) t[i] = -0.25d*Math.PI + i * dt;
				
		Point2D[] pts = new Point2D[ISOSAMPLES + 1];
		for(int i = 0; i <= ISOSAMPLES; ++i) 
			pts[i] = new Point2D.Double(
					
					gapx + (2*radius-width) + 
					(width/0.41421356d)*( (FastMath.tan(FastMath.atan(1d/FastMath.cos(t[i]))*0.5d)*FastMath.cos(t[i]))) ,
					
					gapy + 2*radius - (2*radius-width)/2 + 
					(width/0.41421356d)*( (FastMath.tan(FastMath.atan(1d/FastMath.cos(t[i]))*0.5d)*FastMath.sin(t[i]))) );
		
		drawACurve(g, pts);
	}
	
	public static final void drawSSTHex(final Graphics2D g,
			  final int gapx, final int gapy, final int radius, int width) {

		

		g.setColor(Color.BLACK);
		final BasicStroke dashed = new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
		g.setStroke(dashed);


		Line2D axis1 = new Line2D.Double(gapx + (2*radius-width),
				gapy + 2*radius - (2*radius-width)/2,
				
				gapx + 2*radius,
				gapy + 2*radius - (2*radius-width)/2);
		
		Line2D axis2 = new Line2D.Double(gapx + (2*radius-width),
				gapy + 2*radius - (2*radius-width)/2,
				
				gapx + (2*radius-width) + FastMath.cos(FastMath.PI / 6d)*width,
				gapy + 2*radius - (2*radius-width)/2 -FastMath.sin(FastMath.PI / 6d)*width);	
		
		
		Arc2D arc = new Arc2D.Double(gapx + (2*radius-width) - width,
				gapy + 2*radius - (2*radius-width)/2 - width,
				2*width,
				2*width,
				0,
				30,				
				Arc2D.OPEN);
		
		g.draw(axis1);
		g.draw(axis2);
		g.draw(arc);

	}
	
	
	public static final void drawSSTTetr(final Graphics2D g,
			  final int gapx, final int gapy, final int radius, int width) {

		

		g.setColor(Color.BLACK);
		final BasicStroke dashed = new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
		g.setStroke(dashed);


		Line2D axis1 = new Line2D.Double(gapx + (2*radius-width),
				gapy + 2*radius - (2*radius-width)/2,
				
				gapx + 2*radius,
				gapy + 2*radius - (2*radius-width)/2);
		
		Line2D axis2 = new Line2D.Double(gapx + (2*radius-width),
				gapy + 2*radius - (2*radius-width)/2,
				
				gapx + (2*radius-width) + FastMath.cos(FastMath.PI / 4d)*width,
				gapy + 2*radius - (2*radius-width)/2 -FastMath.sin(FastMath.PI / 4d)*width);	
		
		
		Arc2D arc = new Arc2D.Double(gapx + (2*radius-width) - width,
				gapy + 2*radius - (2*radius-width)/2 - width,
				2*width,
				2*width,
				0,
				45,				
				Arc2D.OPEN);
		
		g.draw(axis1);
		g.draw(axis2);
		g.draw(arc);
	
	}
	
	
	public static final void drawSSTOrth(final Graphics2D g,
			  final int gapx, final int gapy, final int radius, int width) {

		

		g.setColor(Color.BLACK);
		final BasicStroke dashed = new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
		g.setStroke(dashed);


		Line2D axis1 = new Line2D.Double(gapx + (2*radius-width),
				gapy + 2*radius - (2*radius-width)/2,
				
				gapx + 2*radius,
				gapy + 2*radius - (2*radius-width)/2);
		
		Line2D axis2 = new Line2D.Double(gapx + (2*radius-width),
				gapy + 2*radius - (2*radius-width)/2,
				
				gapx + (2*radius-width),
				gapy + 2*radius - (2*radius-width)/2 -width);	
		
		
		Arc2D arc = new Arc2D.Double(gapx + (2*radius-width) - width,
				gapy + 2*radius - (2*radius-width)/2 - width,
				2*width,
				2*width,
				0,
				90,				
				Arc2D.OPEN);
		
		g.draw(axis1);
		g.draw(axis2);
		g.draw(arc);
	
	}

}
