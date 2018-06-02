package com.example.demo.entity;
// Generated Jun 1, 2018 1:13:31 PM by Hibernate Tools 5.2.3.Final

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * UserRole generated by hbm2java
 */
@Entity
@Table(name = "user_role", catalog = "jpalogindb", uniqueConstraints = @UniqueConstraint(columnNames = { "USER_ID",
		"ROLE_ID" }))
public class UserRole implements java.io.Serializable {

	private long id;
	private AppRole appRole;
	private AppUser appUser;

	public UserRole() {
	}

	public UserRole(long id, AppRole appRole, AppUser appUser) {
		this.id = id;
		this.appRole = appRole;
		this.appUser = appUser;
	}

	@Id

	@Column(name = "ID", unique = true, nullable = false)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ROLE_ID", nullable = false)
	public AppRole getAppRole() {
		return this.appRole;
	}

	public void setAppRole(AppRole appRole) {
		this.appRole = appRole;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID", nullable = false)
	public AppUser getAppUser() {
		return this.appUser;
	}

	public void setAppUser(AppUser appUser) {
		this.appUser = appUser;
	}

}
