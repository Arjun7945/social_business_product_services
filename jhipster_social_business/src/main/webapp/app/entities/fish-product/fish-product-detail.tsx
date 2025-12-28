import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './fish-product.reducer';

export const FishProductDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const fishProductEntity = useAppSelector(state => state.fishProduct.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="fishProductDetailsHeading">
          <Translate contentKey="whatsappProductServiceProApp.fishProduct.detail.title">FishProduct</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{fishProductEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="whatsappProductServiceProApp.fishProduct.name">Name</Translate>
            </span>
          </dt>
          <dd>{fishProductEntity.name}</dd>
          <dt>
            <span id="pricePerKg">
              <Translate contentKey="whatsappProductServiceProApp.fishProduct.pricePerKg">Price Per Kg</Translate>
            </span>
          </dt>
          <dd>{fishProductEntity.pricePerKg}</dd>
          <dt>
            <span id="availableQuantity">
              <Translate contentKey="whatsappProductServiceProApp.fishProduct.availableQuantity">Available Quantity</Translate>
            </span>
            <UncontrolledTooltip target="availableQuantity">
              <Translate contentKey="whatsappProductServiceProApp.fishProduct.help.availableQuantity" />
            </UncontrolledTooltip>
          </dt>
          <dd>{fishProductEntity.availableQuantity}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="whatsappProductServiceProApp.fishProduct.description">Description</Translate>
            </span>
          </dt>
          <dd>{fishProductEntity.description}</dd>
          <dt>
            <span id="isAvailable">
              <Translate contentKey="whatsappProductServiceProApp.fishProduct.isAvailable">Is Available</Translate>
            </span>
          </dt>
          <dd>{fishProductEntity.isAvailable ? 'true' : 'false'}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="whatsappProductServiceProApp.fishProduct.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {fishProductEntity.createdAt ? <TextFormat value={fishProductEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <Translate contentKey="whatsappProductServiceProApp.fishProduct.image">Image</Translate>
          </dt>
          <dd>{fishProductEntity.image ? fishProductEntity.image.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/fish-product" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/fish-product/${fishProductEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default FishProductDetail;
