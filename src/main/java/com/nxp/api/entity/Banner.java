package com.nxp.api.entity;

import java.util.Date;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Banner {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_BANNER")
//	@SequenceGenerator(name = "SEQ_BANNER", sequenceName = "SEQ_BANNER", allocationSize = 1)
	private Long id;

	private String title;

	private String imagePath;

	private String link;

	@Column(updatable = false)
	private String createdBy;

	@Column(updatable = false)
	private Date createdDate;

	private int status;

	private Integer priority;

	private Long operatorId;
	
	@Transient
	private int htmlType; 
}
