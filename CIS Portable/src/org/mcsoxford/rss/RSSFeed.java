/*
 * Copyright (C) 2010 A. Horn
 *
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
 */

package org.mcsoxford.rss;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Data about an RSS feed and its RSS items.
 * 
 * @author Mr Horn
 */
public class RSSFeed extends RSSBase {

  private final java.util.List<RSSItem> items;

  RSSFeed() {
    super(/* initial capacity for category names */ (byte) 3);
    items = new java.util.LinkedList<RSSItem>();
  }

  /**
   * Returns an unmodifiable list of RSS items.
   */
  public java.util.List<RSSItem> getItems() {
    return java.util.Collections.unmodifiableList(items);
  }

  public RSSItem getItem(int pos) throws IndexOutOfBoundsException {
	  return items.get(pos);
  }
  
  void addItem(RSSItem item) {
    items.add(item);
  }
  
  public void sortByDate() {
	  Collections.sort(items);
  }
  
  /**
   * Returns the number of feed items inside this feed
   */
  public int getCount() {
	  return this.items.size();
  }

}

