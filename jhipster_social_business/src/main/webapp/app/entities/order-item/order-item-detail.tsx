import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './order-item.reducer';

export const OrderItemDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const orderItemEntity = useAppSelector(state => state.orderItem.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="orderItemDetailsHeading">
          <Translate contentKey="whatsappProductServiceProApp.orderItem.detail.title">OrderItem</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{orderItemEntity.id}</dd>
          <dt>
            <span id="quantityKg">
              <Translate contentKey="whatsappProductServiceProApp.orderItem.quantityKg">Quantity Kg</Translate>
            </span>
          </dt>
          <dd>{orderItemEntity.quantityKg}</dd>
          <dt>
            <span id="priceAtOrder">
              <Translate contentKey="whatsappProductServiceProApp.orderItem.priceAtOrder">Price At Order</Translate>
            </span>
          </dt>
          <dd>{orderItemEntity.priceAtOrder}</dd>
          <dt>
            <Translate contentKey="whatsappProductServiceProApp.orderItem.product">Product</Translate>
          </dt>
          <dd>
            {orderItemEntity.product ? <Link to={`/fish-product/${orderItemEntity.product.id}`}>{orderItemEntity.product.name}</Link> : ''}
          </dd>
          <dt>
            <Translate contentKey="whatsappProductServiceProApp.orderItem.order">Order</Translate>
          </dt>
          <dd>{orderItemEntity.order ? <Link to={`/customer-order/${orderItemEntity.order.id}`}>{orderItemEntity.order.id}</Link> : ''}</dd>
        </dl>
        <Button tag={Link} to="/order-item" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/order-item/${orderItemEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default OrderItemDetail;
