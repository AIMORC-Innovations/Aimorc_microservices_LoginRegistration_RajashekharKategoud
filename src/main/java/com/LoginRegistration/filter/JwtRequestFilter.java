package com.LoginRegistration.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ResourceProperties.Chain;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.LoginRegistration.helper.JWTUtil;
import com.LoginRegistration.services.LoginServices;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private LoginServices services;

	@Autowired
	private JWTUtil jwtutil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authorization = request.getHeader("Authorization");

		String username = null;
		String jwt = null;

		if (authorization != null && authorization.startsWith("Bearer ")) {
			jwt = authorization.substring(7);
			try {
				username = jwtutil.getUsernameFromToken(jwt);
			} catch (Exception e) {
				e.printStackTrace();
			}

			UserDetails userdetails = this.services.loadUserByUsername(username);

			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

				UsernamePasswordAuthenticationToken usernamePasswordAuthenication = new UsernamePasswordAuthenticationToken(
						userdetails, null, userdetails.getAuthorities());
				usernamePasswordAuthenication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenication);
			} else

			{

				System.out.println("Inavlid token");

			}

		}
		filterChain.doFilter(request, response);
	}
}
