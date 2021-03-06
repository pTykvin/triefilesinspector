package ru.happyfat.integrity.trie;

import java.io.Serializable;

public class FileInfo implements Serializable {
    private String path;
    private String name;
    private String md5;
    private long size;
    private boolean directory;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    @Override
    public String toString() {
        return name + (isDirectory() ? "/" : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FileInfo fileInfo = (FileInfo) o;

        if (size != fileInfo.size) {
            return false;
        }
        if (directory != fileInfo.directory) {
            return false;
        }
        if (!name.equals(fileInfo.name)) {
            return false;
        }
        return md5 != null ? md5.equals(fileInfo.md5) : fileInfo.md5 == null;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (md5 != null ? md5.hashCode() : 0);
        result = 31 * result + (int) (size ^ (size >>> 32));
        result = 31 * result + (directory ? 1 : 0);
        return result;
    }
}
