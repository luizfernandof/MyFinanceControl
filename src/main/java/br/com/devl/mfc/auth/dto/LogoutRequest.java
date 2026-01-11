package br.com.devl.mfc.auth.dto;

public class LogoutRequest {

	private String refreshToken;
	
	public String getRefreshToken() {
		return this.refreshToken;
	}
	
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
