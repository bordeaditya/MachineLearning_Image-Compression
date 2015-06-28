/*
* Author : Aditya Borde
* Function : Image Compression using K-Means Clustering
*/
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;


class Cluster
{
	int rSum =0,gSum =0,bSum =0;
	int rMean =0,gMean=0,bMean=0;
	
	int old_RMean =0,old_GMean =0,old_BMean =0;
	ArrayList<PointRGB> cluserPoints = new ArrayList<PointRGB>();
	
	public int getrSum() {
		return rSum;
	}
	public void setrSum(int rSum) {
		this.rSum = rSum;
	}
	public int getgSum() {
		return gSum;
	}
	public void setgSum(int gSum) {
		this.gSum = gSum;
	}
	public int getbSum() {
		return bSum;
	}
	public void setbSum(int bSum) {
		this.bSum = bSum;
	}
	public int getrMean() {
		return rMean;
	}
	public void setrMean(int rMean) {
		this.rMean = rMean;
	}
	public int getgMean() {
		return gMean;
	}
	public void setgMean(int gMean) {
		this.gMean = gMean;
	}
	public int getbMean() {
		return bMean;
	}
	public void setbMean(int bMean) {
		this.bMean = bMean;
	}
	public int getOld_RMean() {
		return old_RMean;
	}
	public void setOld_RMean(int old_RMean) {
		this.old_RMean = old_RMean;
	}
	public int getOld_GMean() {
		return old_GMean;
	}
	public void setOld_GMean(int old_GMean) {
		this.old_GMean = old_GMean;
	}
	public int getOld_BMean() {
		return old_BMean;
	}
	public void setOld_BMean(int old_BMean) {
		this.old_BMean = old_BMean;
	} 
	
}

class PointRGB
{
	int r,g,b;
	int index;
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getR() {
		return r;
	}

	public void setR(int r) {
		this.r = r;
	}

	public int getG() {
		return g;
	}

	public void setG(int g) {
		this.g = g;
	}

	public int getB() {
		return b;
	}

	public void setB(int b) {
		this.b = b;
	}
	
}

public class KMean {

	public static void main(String[] args) {
		if (args.length < 3){
		    System.out.println("Usage: Kmeans <input-image> <k> <output-image>");
		    return;
		}
		try{
		    BufferedImage originalImage = ImageIO.read(new File(args[0]));
		    int k = Integer.parseInt(args[1]);
		    BufferedImage kmeansJpg = kmeans_helper(originalImage,k);
		    ImageIO.write(kmeansJpg, "jpg", new File(args[2])); 
		    
		}catch(IOException e){
		    System.out.println(e.getMessage());
		}	

	}
	
	
	private static BufferedImage kmeans_helper(BufferedImage originalImage, int k){
		int w=originalImage.getWidth();
		int h=originalImage.getHeight();
		BufferedImage kmeansImage = new BufferedImage(w,h,originalImage.getType());
		Graphics2D g = kmeansImage.createGraphics();
		g.drawImage(originalImage, 0, 0, w,h , null);
		// Read rgb values from the image
		int[] rgb=new int[w*h];
		int count=0;
		for(int i=0;i<w;i++){
		    for(int j=0;j<h;j++){
			rgb[count++]=kmeansImage.getRGB(i,j);
		    }
		}
		// Call kmeans algorithm: update the rgb values
		kmeans(rgb,k);

		// Write the new rgb values to the image
		count=0;
		for(int i=0;i<w;i++){
		    	for(int j=0;j<h;j++){
		    		kmeansImage.setRGB(i,j,rgb[count++]);
		    	}
			}
			return kmeansImage;
	    }

	    
		/*** Function Starts here
		 * kmeans
		 * @param rgb
		 * @param k
		 */
		// K-Means Function:
		private static void kmeans(int[] rgb, int k){
			
			System.out.println("***K-Means***");
			System.out.println("--Running--");
			Cluster[] cluster = new Cluster[k];
			
			ArrayList<Integer> randomMean = new ArrayList<Integer>();
			int i=0;
			// Select Random Value for the cluster Mean
			for(i=0;i<k;i++)
			{
				int randomValue = GetRandom(0, rgb.length);
				if(!randomMean.contains(randomValue))
				{
					randomMean.add(randomValue);
				}
			}
			
			// Get the Mean value Colors and assign the point to the cluster.
			for(i=0;i<randomMean.size();i++)
			{
				Color c = new Color(rgb[randomMean.get(i)]);
				cluster[i] = new Cluster();
				cluster[i].setrMean(c.getRed());
				cluster[i].setgMean(c.getGreen());
				cluster[i].setbMean(c.getBlue());
			}
			
			// Continue till Mean doesn't change
			while(IsMeanChanged(cluster))
			{
				//clear cluster lists
				for(Cluster c:cluster)
					ClearClusterLists(c);
				
				// classify all the points
				for(i=0;i<rgb.length;i++)
					ClassifyPoint(cluster,rgb[i],i);
				
				// calculate new mean of each cluster
				for(Cluster c:cluster)
					CalculateNewMean(c);

			}
			
			// fill the new RGB values in the Array - rgb
			for(Cluster c:cluster)
				FillRGBInArray(c,rgb);
			
			System.out.println("Program Execution completed.");
		}
		
		// Fill the RGB array - After Convergence.
		private static void FillRGBInArray(Cluster c, int[] rgb) {
			
			Color color = new Color(c.getrMean(),c.getgMean(),c.getbMean());
			for(PointRGB point : c.cluserPoints)
				rgb[point.getIndex()] = color.getRGB();
		}


		// Clear cluster lists and reset the Sum values.
		private static void ClearClusterLists(Cluster c) {
			c.cluserPoints.clear();
			c.rSum =0;
			c.gSum =0;
			c.bSum =0;
		}


		// Calculate new mean of the cluster
		private static void CalculateNewMean(Cluster c) {
			
			int rMean = 0,gMean=0,bMean =0;
			// Set the Old Mean <- mean value
			c.setOld_RMean(c.getrMean());
			c.setOld_GMean(c.getgMean());
			c.setOld_BMean(c.getbMean());
			if(c.cluserPoints.size()!=0)
			{
				rMean = c.getrSum()/c.cluserPoints.size();
				gMean = c.getgSum()/c.cluserPoints.size();
				bMean = c.getbSum()/c.cluserPoints.size();
			}
			else
			{
				rMean = c.getrMean();
				gMean = c.getgMean();
				bMean = c.getbMean();
			}
			// Set the new Mean values
			c.setrMean(rMean);
			c.setgMean(gMean);
			c.setbMean(bMean);
			
		}


		// Classify the point in the graph
		private static void ClassifyPoint(Cluster[] cluster, int point, int index) {
			
			double minDistance = Double.MAX_VALUE;
			int classified = 0;
			int clusterNumber =0;
			Color color = new Color(point);
			PointRGB pointRGB = new PointRGB();
			pointRGB.setR(color.getRed());
			pointRGB.setG(color.getGreen());
			pointRGB.setB(color.getBlue());
			pointRGB.setIndex(index);
			// Calculate distance from each cluster
			for(clusterNumber=0;clusterNumber<cluster.length;clusterNumber++)
			{
				double curDist = GetDistance(cluster[clusterNumber],pointRGB);
				if(Double.compare(minDistance, curDist)>0)
				{
					minDistance = curDist;
					classified = clusterNumber;
				}
			}
			// caculate the sum value of point
			cluster[classified].setrSum(cluster[classified].getrSum()+pointRGB.getR());
			cluster[classified].setgSum(cluster[classified].getgSum()+pointRGB.getG());
			cluster[classified].setbSum(cluster[classified].getbSum()+pointRGB.getB());
			cluster[classified].cluserPoints.add(pointRGB);
			
		}

		// Get the distance from the mean
		private static double GetDistance(Cluster c, PointRGB point) {
			
			double dist = Math.sqrt(Math.pow(c.getrMean()-point.getR(), 2)+ Math.pow(c.getgMean()-point.getG(), 2)+ Math.pow(c.getbMean()-point.getB(), 2));
			return dist;
		}


		// Check if the Mean is same as before OR not.
		private static boolean IsMeanChanged(Cluster[] cluster) {
			
			Color colorOld,colorNew;
			for(Cluster c : cluster)
			{
				colorOld = new Color(c.getOld_RMean(),c.getOld_GMean(),c.getOld_BMean());
				colorNew = new Color(c.getrMean(),c.getgMean(),c.getbMean());
				if(colorOld.getRGB()!=colorNew.getRGB())
					return true;
			}
			return false;
		}


		// Generate Random Integer.
		public static int GetRandom(int minimum,int maximum)
		{
			 int randomInt = minimum + (int)(Math.random()*maximum); 
			 return randomInt;
		}


}
