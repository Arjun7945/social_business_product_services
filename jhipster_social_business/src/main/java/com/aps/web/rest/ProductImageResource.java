package com.aps.web.rest;

import com.aps.repository.ProductImageRepository;
import com.aps.service.ProductImageService;
import com.aps.service.dto.ProductImageDTO;
import com.aps.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.aps.domain.ProductImage}.
 */
@RestController
@RequestMapping("/api/product-images")
public class ProductImageResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProductImageResource.class);

    private static final String ENTITY_NAME = "productImage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductImageService productImageService;

    private final ProductImageRepository productImageRepository;

    public ProductImageResource(ProductImageService productImageService,
            ProductImageRepository productImageRepository) {
        this.productImageService = productImageService;
        this.productImageRepository = productImageRepository;
    }

    /**
     * {@code POST  /product-images} : Create a new productImage.
     *
     * @param productImageDTO the productImageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new productImageDTO, or with status
     *         {@code 400 (Bad Request)} if the productImage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<ProductImageDTO> createProductImage(
            @RequestParam(value = "productId", required = false) Long productId,
            @RequestParam("file") MultipartFile file) throws URISyntaxException, IOException {
        LOG.debug("REST request to save ProductImage : {}", file.getOriginalFilename());

        ProductImageDTO productImageDTO = new ProductImageDTO();
        productImageDTO.setMimeType(file.getContentType());
        productImageDTO.setImageData(file.getBytes());
        if (productId != null) {
            com.aps.service.dto.FishProductDTO fishProductDTO = new com.aps.service.dto.FishProductDTO();
            fishProductDTO.setId(productId);
            productImageDTO.setProduct(fishProductDTO);
        }

        ProductImageDTO result = productImageService.save(productImageDTO);
        return ResponseEntity.created(new URI("/api/product-images/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME,
                        result.getId().toString()))
                .body(result);
    }

    /**
     * {@code PUT  /product-images/:id} : Updates an existing productImage.
     *
     * @param id              the id of the productImageDTO to save.
     * @param productImageDTO the productImageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated productImageDTO,
     *         or with status {@code 400 (Bad Request)} if the productImageDTO is
     *         not valid,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         productImageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductImageDTO> updateProductImage(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody ProductImageDTO productImageDTO) throws URISyntaxException {
        LOG.debug("REST request to update ProductImage : {}, {}", id, productImageDTO);
        if (productImageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productImageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productImageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        productImageDTO = productImageService.update(productImageDTO);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
                        productImageDTO.getId().toString()))
                .body(productImageDTO);
    }

    /**
     * {@code PATCH  /product-images/:id} : Partial updates given fields of an
     * existing productImage, field will ignore if it is null
     *
     * @param id              the id of the productImageDTO to save.
     * @param productImageDTO the productImageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated productImageDTO,
     *         or with status {@code 400 (Bad Request)} if the productImageDTO is
     *         not valid,
     *         or with status {@code 404 (Not Found)} if the productImageDTO is not
     *         found,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         productImageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProductImageDTO> partialUpdateProductImage(
            @PathVariable(value = "id", required = false) final Long id,
            @NotNull @RequestBody ProductImageDTO productImageDTO) throws URISyntaxException {
        LOG.debug("REST request to partial update ProductImage partially : {}, {}", id, productImageDTO);
        if (productImageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productImageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productImageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProductImageDTO> result = productImageService.partialUpdate(productImageDTO);

        return ResponseUtil.wrapOrNotFound(
                result,
                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
                        productImageDTO.getId().toString()));
    }

    /**
     * {@code GET  /product-images} : get all the productImages.
     *
     * @param pageable  the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is
     *                  applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of productImages in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ProductImageDTO>> getAllProductImages(
            @org.springdoc.core.annotations.ParameterObject Pageable pageable,
            @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload) {
        LOG.debug("REST request to get a page of ProductImages");
        Page<ProductImageDTO> page;
        if (eagerload) {
            page = productImageService.findAllWithEagerRelationships(pageable);
        } else {
            page = productImageService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil
                .generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /product-images/:id} : get the "id" productImage.
     *
     * @param id the id of the productImageDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the productImageDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductImageDTO> getProductImage(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ProductImage : {}", id);
        Optional<ProductImageDTO> productImageDTO = productImageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(productImageDTO);
    }

    /**
     * {@code DELETE  /product-images/:id} : delete the "id" productImage.
     *
     * @param id the id of the productImageDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductImage(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ProductImage : {}", id);
        productImageService.delete(id);
        return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                .build();
    }

    /**
     * {@code GET  /product-images/public/:id/content} : get the "id" productImage
     * content.
     */
    @GetMapping("/public/{id}/content")
    public ResponseEntity<byte[]> getPublicImageContent(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ProductImage content : {}", id);
        Optional<ProductImageDTO> productImageDTOOpt = productImageService.findOne(id);

        if (productImageDTOOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ProductImageDTO productImageDTO = productImageDTOOpt.get();
        byte[] imageData = productImageDTO.getImageData();
        String mimeType = productImageDTO.getMimeType();

        if (imageData == null || imageData.length == 0) {
            return ResponseEntity.notFound().build();
        }

        if (mimeType == null || mimeType.isEmpty()) {
            mimeType = "image/jpeg";
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, mimeType)
                .body(imageData);
    }

    /**
     * {@code GET  /product-images/public/uuid/:uuid/content} : get the productImage
     * content by UUID.
     */
    @GetMapping("/public/uuid/{uuid}/content")
    public ResponseEntity<byte[]> getPublicImageContentByUuid(@PathVariable("uuid") String uuid) {
        LOG.debug("REST request to get ProductImage content by UUID : {}", uuid);

        // Reconstruct the partial URL or search by the UUID part if we stored just the
        // UUID?
        // The service stores the FULL URL. So we should search by the full URL pattern.
        // URL format: "/api/product-images/public/uuid/" + uuid + "/content"
        String lookupUrl = "/api/product-images/public/uuid/" + uuid + "/content";

        Optional<com.aps.domain.ProductImage> productImageOpt = productImageRepository.findByImageUrl(lookupUrl);

        if (productImageOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        com.aps.domain.ProductImage image = productImageOpt.get();
        byte[] imageData = image.getImageData();
        String mimeType = image.getMimeType();

        if (imageData == null || imageData.length == 0) {
            return ResponseEntity.notFound().build();
        }

        if (mimeType == null || mimeType.isEmpty()) {
            mimeType = "image/jpeg";
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, mimeType)
                .body(imageData);
    }
}
