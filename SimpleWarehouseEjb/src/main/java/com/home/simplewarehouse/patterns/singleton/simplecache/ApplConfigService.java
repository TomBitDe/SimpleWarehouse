package com.home.simplewarehouse.patterns.singleton.simplecache;

import java.util.List;

import com.home.simplewarehouse.patterns.singleton.simplecache.model.ApplConfig;

/**
 * Interface definition for application configuration management.
 */
public interface ApplConfigService {
	/**
	 * Deliver all entries in ApplConfig
	 *
	 * @return the entries
	 */
	public List<ApplConfig> getContent();

	/**
	 * Deliver entries in ApplConfig based on offset and count
	 *
     * @param offset the position to start fetching entries
     * @param count  the number of fetches to do
	 *
	 * @return the entries
	 */
	public List<ApplConfig> getContent(int offset, int count);

	/**
	 * Get an ApplConfig item by its Primary Key id
	 *
	 * @param keyVal the Key value
	 *
	 * @return the matching ApplConfig item or null in case of no match
	 */
	public ApplConfig getById(String keyVal);

	/**
	 * Persist the given ApplConfig item
	 *
	 * @param config the item data
	 *
	 * @return the created item
	 */
	public ApplConfig create(ApplConfig config);

	/**
	 * Update the given ApplConfig item
	 *
	 * @param config the item data
	 *
	 * @return the updated item or null if the update failed
	 */
	public ApplConfig update(ApplConfig config);

	/**
	 * Remove an ApplConfig item by Primary Key access
	 *
	 * @param keyVal the Key value
	 *
	 * @return the deleted ApplConfig item or null
	 */
	public ApplConfig delete(String keyVal);

	/**
	 * Count the ApplConfig items
	 *
	 * @return the number of ApplConfig items
	 */
	public int count();

	/**
	 * Refresh the ApplConfig from source
	 */
	public void refresh();
}
