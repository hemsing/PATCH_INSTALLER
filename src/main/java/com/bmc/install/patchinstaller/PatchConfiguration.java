package com.bmc.install.patchinstaller;

import org.w3c.dom.Node;

public class PatchConfiguration {

	/**
	 * @return the productname_
	 */
	public String getProductname_() {
		return productname_;
	}

	/**
	 * @param productname_
	 *            the productname_ to set
	 */
	public void setProductname_(String productname_) {
		this.productname_ = productname_;
	}

	/**
	 * @return the productversion_
	 */
	public String getProductversion_() {
		return productversion_;
	}

	/**
	 * @param productversion_
	 *            the productversion_ to set
	 */
	public void setProductversion_(String productversion_) {
		this.productversion_ = productversion_;
	}

	/**
	 * @return the mincompatibleversion_
	 */
	public String getMincompatibleversion_() {
		return mincompatibleversion_;
	}

	/**
	 * @param mincompatibleversion_
	 *            the mincompatibleversion_ to set
	 */
	public void setMincompatibleversion_(String mincompatibleversion_) {
		this.mincompatibleversion_ = mincompatibleversion_;
	}

	/**
	 * @return the installSequence_
	 */
	public Node getInstallSequence_() {
		return installSequence_;
	}

	/**
	 * @param installSequence_
	 *            the installSequence_ to set
	 */
	public void setInstallSequence_(Node installSequence_) {
		this.installSequence_ = installSequence_;
	}

	/**
	 * @return the installSequence_
	 */
	public Node getUnInstallSequence_() {
		return unInstallSequence_;
	}

	/**
	 * @param installSequence_
	 *            the installSequence_ to set
	 */
	public void setUnInstallSequence_(Node unInstallSequence_) {
		this.unInstallSequence_ = unInstallSequence_;
	}

	/**
	 * @return the index_
	 */
	public int getIndex_() {
		return index_;
	}

	/**
	 * @param index_
	 *            the index_ to set
	 */
	public void setIndex_(int index_) {
		this.index_ = index_;
	}

	private String productversion_;
	private String mincompatibleversion_;
	private String productname_;
	private Node installSequence_;
	private Node unInstallSequence_;
	private int index_;

}
