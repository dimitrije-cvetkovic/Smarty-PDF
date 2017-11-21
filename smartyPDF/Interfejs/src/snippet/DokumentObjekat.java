package snippet;

public class DokumentObjekat {
	
	 private String sadrzaj;
	 private String[] metadataNames = null;
	 private String[] metadataValues = null;
	 private String naziv;
		
	   public DokumentObjekat()
	   {
		   metadataNames = new String[100];
		   metadataValues = new String[100];
	   }
	   
	   public DokumentObjekat(int size)
	   {
		   metadataNames = new String[size];
		   metadataValues = new String[size];
	   }
		
	   public String getSadrzaj() {
	      return sadrzaj;
	   }
		
	   public void setSadrzaj(String sad) {
	      this.sadrzaj = sad;
	   }
		
	   public String getMetadataNames(int i) {
	       if(i<metadataNames.length)
	    	   return metadataNames[i];
	       return null;
	   }
		
	   public void setMetadataNames(String mtd, int i) {
	      if(i<metadataNames.length)
	    	  this.metadataNames[i] =  mtd;
	   }
	   
	   public String getMetadataValues(int i) {
	       if(i<metadataValues.length)
	    	   return metadataValues[i];
	       return null;
	   }
	   
	   public void setMetadataValues(String mtd, int i) {
		      if(i<metadataValues.length)
		    	  this.metadataValues[i] =  mtd;
		   }
	   
	   public String toString(){
	      return "Dokument ima sadrzaj: "+sadrzaj;
	   }   
	   
	   public String forNameGiveValue(String str) {
	       for(int i=0; i<metadataNames.length; i++)
	       {
	    	   if(metadataNames[i].compareToIgnoreCase(str)==0)
	    		   return metadataValues[i];
	       }
	       return null;
	   }
	   
	   public int getMetadataNamesCount()
	   {
		   return metadataNames.length;
	   }

	public String getNaziv() {
		return naziv;
	}

	public void setNaziv(String naziv) {
		this.naziv = naziv;
	}
}
