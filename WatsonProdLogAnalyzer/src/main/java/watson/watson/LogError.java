package watson.watson;

public class LogError {
	   String error;
	   int importance;
	   String solution;
	   
	   public LogError(String error, String solution) {
		   this.error = error;
		   this.solution = solution;
	   }
	   
	   public void setImportance(int logImportance) {
		   importance = logImportance;
	   }
	   
	   public void setSolution(String logSolution) {
		   solution = logSolution;
	   }
	   
}