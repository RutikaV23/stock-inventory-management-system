package com.rutika.inventory.service.impl;

import com.rutika.inventory.constants.MessageConstants;
import com.rutika.inventory.dto.request.ProductRequest;
import com.rutika.inventory.dto.response.ProductResponse;
import com.rutika.inventory.entity.Product;
import com.rutika.inventory.enums.ProductStatus;
import com.rutika.inventory.exception.ResourceNotFoundException;
import com.rutika.inventory.mapper.ProductMapper;
import com.rutika.inventory.repository.ProductRepository;
import com.rutika.inventory.response.PageResponse;
import com.rutika.inventory.service.interfaces.ProductService;
import com.rutika.inventory.validator.ProductValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductValidator productValidator;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        productValidator.validateCreate(request);
        Product product = productMapper.toEntity(request);
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.PRODUCT, "id", id));
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getAllProducts(int page, int size) {
        Page<Product> productPage = productRepository.findAll(PageRequest.of(page, size));
        return PageResponse.<ProductResponse>builder()
                .content(productPage.getContent().stream()
                        .map(productMapper::toResponse)
                        .toList())
                .page(productPage.getNumber())
                .size(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .first(productPage.isFirst())
                .last(productPage.isLast())
                .build();
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(String id, ProductRequest request) {
        productValidator.validateUpdate(id, request);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.PRODUCT, "id", id));
        productMapper.updateEntityFromRequest(request, product);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponse(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.PRODUCT, "id", id));
        product.setStatus(ProductStatus.INACTIVE);
        productRepository.save(product);
    }
}
