package demo.repository;

/**
 * Lightweight projection for populating the contact-form product dropdown.
 * Only id and ten (name) are fetched — not moTa, features, specs, etc.
 * This replaces the full findAll() call that was loading all 20+ fields
 * per product just to render "<option value='${p.ten}'>${p.ten}</option>".
 */
public interface SanPhamDropdown {
    Long getId();
    String getTen();
}
