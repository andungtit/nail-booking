package com.nailroom.booking.service.base;

import com.nailroom.booking.dto.base.BaseListRequest;
import com.nailroom.booking.dto.response.BaseListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.function.Function;

public abstract class BaseListService<E, R extends BaseListRequest> {

    protected abstract JpaSpecificationExecutor<E> executor();

    protected abstract Specification<E> buildSpecification(R request);

    public Page<E> list(R request) {
        Pageable pageable = request.toPageable();
        Specification<E> spec = buildSpecification(request);
        return executor().findAll(spec, pageable);
    }

    public <D> BaseListResponse<D> list(R request, Function<E, D> mapper) {
        return BaseListResponse.of(list(request).map(mapper));
    }

    public BaseListResponse<E> listResponse(R request) {
        return BaseListResponse.of(list(request));
    }
}
