package com.expensemanagement.pojo;

public class Category {
    private int id;
    private String name;
    private String type;
    private boolean isDefault; // Để xác định danh mục cố định

    public Category(int id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public Category(int id, String name, String type, boolean isDefault) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.isDefault = isDefault;
    }

    public Category(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.name; // Trả về tên danh mục
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return nếu danh mục là mặc định (cố định)
     */
    public boolean isDefault() {
        return isDefault;
    }

    /**
     * @param isDefault set trạng thái mặc định
     */
    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    // Phương thức validate để kiểm tra ràng buộc
    public void validate() throws IllegalArgumentException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được để trống!");
        }
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Loại danh mục không được để trống!");
        }
        if (!name.matches("^[\\p{L}\\s]+$")) {
            throw new IllegalArgumentException("Tên danh mục chỉ được chứa chữ cái và khoảng trắng, không chứa số hoặc ký tự đặc biệt!");
        }
        if (!type.equals("Chi") && !type.equals("Thu")) {
            throw new IllegalArgumentException("Loại danh mục phải là 'Chi' hoặc 'Thu'!");
        }
    }
}