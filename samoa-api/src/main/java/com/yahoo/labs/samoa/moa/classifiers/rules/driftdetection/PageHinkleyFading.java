package com.yahoo.labs.samoa.moa.classifiers.rules.driftdetection;

/*
 * #%L
 * SAMOA
 * %%
 * Copyright (C) 2013 - 2014 Yahoo! Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

/**
 * Page-Hinkley Test with more weight for recent instances.
 *
 */

public class PageHinkleyFading extends PageHinkleyTest {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7110953184708812339L;
	private double fadingFactor=0.99;

	public PageHinkleyFading() {
		super();
	}
	
	public PageHinkleyFading(double threshold, double alpha) {
		super(threshold, alpha);
	}
	protected double instancesSeen;

	@Override
	public void reset() {

		super.reset();
		this.instancesSeen=0;

	}

	@Override
	public boolean update(double error) {
		this.instancesSeen=1+fadingFactor*this.instancesSeen;
		double absolutError = Math.abs(error);

		this.sumAbsolutError = fadingFactor*this.sumAbsolutError + absolutError;
		if (this.instancesSeen > 30) {
			double mT = absolutError - (this.sumAbsolutError / this.instancesSeen) - this.alpha;
			this.cumulativeSum = this.cumulativeSum + mT; // Update the cumulative mT sum
			if (this.cumulativeSum < this.minimumValue) { // Update the minimum mT value if the new mT is smaller than the current minimum
				this.minimumValue = this.cumulativeSum;
			}
			return (((this.cumulativeSum - this.minimumValue) > this.threshold));
		}
		return false;
	}
	
	@Override
	public PageHinkleyTest getACopy() {
		PageHinkleyFading newTest = new PageHinkleyFading(this.threshold, this.alpha);
		this.copyFields(newTest);
		return newTest;
	}
	
	@Override
	protected void copyFields(PageHinkleyTest newTest) {
		super.copyFields(newTest);
		PageHinkleyFading newFading = (PageHinkleyFading) newTest;
		newFading.fadingFactor = this.fadingFactor;
		newFading.instancesSeen = this.instancesSeen;
	}
}