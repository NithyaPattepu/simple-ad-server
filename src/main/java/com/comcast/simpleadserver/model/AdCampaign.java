
package com.comcast.simpleadserver.model;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * AdCampaign POJO class
 * 
 * @author nithya
 *
 */
public class AdCampaign {
	
	private String partnerId;
	
	private String duration;
	
	private String adContent;
	
	@JsonIgnore
	private Date expireDate;

	
	/**
	 * Gets the {@link #partnerId}.
	 *
	 * @return Returns the {@link #partnerId}.
	 */
	public String getPartnerId() {
		return partnerId;
	}

	/**
	 * Sets the {@link #partnerId}.
	 *
	 * @param partnerId The {@link #partnerId} to set.
	 */
	@JsonProperty("partner_id")
	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	
	/**
	 * Gets the {@link #duration}.
	 *
	 * @return Returns the {@link #duration}.
	 */
	public String getDuration() {
		return duration;
	}

	/**
	 * Sets the {@link #duration}.
	 *
	 * @param duration The {@link #duration} to set.
	 */
	public void setDuration(String duration) {
		this.duration = duration;
	}

	
	/**
	 * Gets the {@link #adContent}.
	 *
	 * @return Returns the {@link #adContent}.
	 */
	public String getAdContent() {
		return adContent;
	}

	/**
	 * Sets the {@link #adContent}.
	 *
	 * @param adContent The {@link #adContent} to set.
	 */
	@JsonProperty("ad_content")
	public void setAdContent(String adContent) {
		this.adContent = adContent;
	}

	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof AdCampaign)) {
			return false;
		}
		AdCampaign castOther = (AdCampaign) other;
		return new EqualsBuilder().append(partnerId, castOther.partnerId).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(partnerId).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE).append("partnerId", partnerId)
				.append("duration", duration).append("adContent", adContent).append("expireDate", expireDate)
				.toString();
	}
	

}
