import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './returned-order-item.reducer';

export const ReturnedOrderItemDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const returnedOrderItemEntity = useAppSelector(state => state.returnedOrderItem.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="returnedOrderItemDetailsHeading">
          <Translate contentKey="whatsappProductServiceProApp.returnedOrderItem.detail.title">ReturnedOrderItem</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{returnedOrderItemEntity.id}</dd>
          <dt>
            <span id="quantity">
              <Translate contentKey="whatsappProductServiceProApp.returnedOrderItem.quantity">Quantity</Translate>
            </span>
          </dt>
          <dd>{returnedOrderItemEntity.quantity}</dd>
          <dt>
            <span id="productComment">
              <Translate contentKey="whatsappProductServiceProApp.returnedOrderItem.productComment">Product Comment</Translate>
            </span>
          </dt>
          <dd>{returnedOrderItemEntity.productComment}</dd>
          <dt>
            <Translate contentKey="whatsappProductServiceProApp.returnedOrderItem.product">Product</Translate>
          </dt>
          <dd>
            {returnedOrderItemEntity.product ? (
              <Link to={`/fish-product/${returnedOrderItemEntity.product.id}`}>{returnedOrderItemEntity.product.name}</Link>
            ) : (
              ''
            )}
          </dd>
          <dt>
            <Translate contentKey="whatsappProductServiceProApp.returnedOrderItem.returnedOrder">Returned Order</Translate>
          </dt>
          <dd>
            {returnedOrderItemEntity.returnedOrder ? (
              <Link to={`/returned-order/${returnedOrderItemEntity.returnedOrder.id}`}>{returnedOrderItemEntity.returnedOrder.id}</Link>
            ) : (
              ''
            )}
          </dd>
        </dl>
        <Button tag={Link} to="/returned-order-item" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/returned-order-item/${returnedOrderItemEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ReturnedOrderItemDetail;
