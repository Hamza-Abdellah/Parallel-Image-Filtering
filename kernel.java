/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

/**
 *
 * @author hamza
 */
import java.awt.image.BufferedImage;
class kernel extends Thread{
    BufferedImage imageInput = null;
	BufferedImage imageOutput = null;
	int w0,h0,w1,h1;
	int size;
	float matrix[];
	
	public kernel(BufferedImage imageInput,BufferedImage imageOutput,float matrix[],int size,int w0,int h0,int w1, int h1) {
		this.imageInput = imageInput;
		this.imageOutput = imageOutput;
		this.matrix = matrix;
		this.size = size;
		this.w0 = w0;
		this.h0 = h0;
		this.w1 = w1;
		this.h1 = h1;
	}
	
	public void run() {
		int p,a,r,g,b;
		int i,j,x,y;
		int a1,r1,g1,b1;
		int k,radius; 
		
		radius = (size-1)/2;
		for(i=w0;i<w1;i++){
	    	for(j=h0;j<h1;j++){
	    		a1=r1=g1=b1=0;
	    		k=0;
	    		if(i>=radius && i<imageInput.getWidth()-radius && j>=radius && j<imageInput.getHeight()-radius){
	    			for(x=i-radius;x<=i+radius;x++){
	    				
		    			for(y=j-radius;y<=j+radius;y++){
		    				p = imageInput.getRGB(x,y); 
		    				
		    	    		r = (p>>16) & 0xff;
		    	    		g = (p>>8) & 0xff;
		    	    		b = p & 0xff;
		    	    		r1 += r*matrix[k];
		    	    		g1 += g*matrix[k];
		    	    		b1 += b*matrix[k];
		    	    		k++;
		    			}
		    		}
	    		}
	    		p = 0;
	    		p = (r1<<16) | (g1<<8) | b1;
	    		imageOutput.setRGB(i, j, p);
	    	}
	    }
		
	}
    
}
