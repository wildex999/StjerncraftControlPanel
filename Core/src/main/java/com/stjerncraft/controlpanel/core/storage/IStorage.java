package com.stjerncraft.controlpanel.core.storage;

/**
 * Generic Key/Value Storage Interface.
 * Allowing storage without explicitly defining if it's on a flat file, database, memory etc.
 * @author Wildex999
 *
 */
public interface IStorage {
	enum StorageType {
		Permanent, //Usually written to a disk.
		Volatile //Stored in memory or in other ways not stored permanently.
	}
	
	StorageType GetStorageType();
	
	void SetIntegerValue(String key, int value);
	int GetIntegerValue(String key);
	
}
