import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './cart-item.reducer';

export const CartItemDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const cartItemEntity = useAppSelector(state => state.cartItem.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="cartItemDetailsHeading">
          <Translate contentKey="whatsappProductServiceProApp.cartItem.detail.title">CartItem</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{cartItemEntity.id}</dd>
          <dt>
            <span id="quantityKg">
              <Translate contentKey="whatsappProductServiceProApp.cartItem.quantityKg">Quantity Kg</Translate>
            </span>
          </dt>
          <dd>{cartItemEntity.quantityKg}</dd>
          <dt>
            <span id="addedAt">
              <Translate contentKey="whatsappProductServiceProApp.cartItem.addedAt">Added At</Translate>
            </span>
          </dt>
          <dd>{cartItemEntity.addedAt ? <TextFormat value={cartItemEntity.addedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <Translate contentKey="whatsappProductServiceProApp.cartItem.product">Product</Translate>
          </dt>
          <dd>{cartItemEntity.product ? cartItemEntity.product.name : ''}</dd>
          <dt>
            <Translate contentKey="whatsappProductServiceProApp.cartItem.cart">Cart</Translate>
          </dt>
          <dd>{cartItemEntity.cart ? cartItemEntity.cart.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/cart-item" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/cart-item/${cartItemEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CartItemDetail;
