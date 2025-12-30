import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './return-status-history.reducer';

export const ReturnStatusHistoryDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const returnStatusHistoryEntity = useAppSelector(state => state.returnStatusHistory.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="returnStatusHistoryDetailsHeading">
          <Translate contentKey="whatsappProductServiceProApp.returnStatusHistory.detail.title">ReturnStatusHistory</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{returnStatusHistoryEntity.id}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="whatsappProductServiceProApp.returnStatusHistory.status">Status</Translate>
            </span>
          </dt>
          <dd>{returnStatusHistoryEntity.status}</dd>
          <dt>
            <span id="changeTime">
              <Translate contentKey="whatsappProductServiceProApp.returnStatusHistory.changeTime">Change Time</Translate>
            </span>
          </dt>
          <dd>
            {returnStatusHistoryEntity.changeTime ? (
              <TextFormat value={returnStatusHistoryEntity.changeTime} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="whatsappProductServiceProApp.returnStatusHistory.returnedOrder">Returned Order</Translate>
          </dt>
          <dd>
            {returnStatusHistoryEntity.returnedOrder ? (
              <Link to={`/returned-order/${returnStatusHistoryEntity.returnedOrder.id}`}>{returnStatusHistoryEntity.returnedOrder.id}</Link>
            ) : (
              ''
            )}
          </dd>
        </dl>
        <Button tag={Link} to="/return-status-history" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/return-status-history/${returnStatusHistoryEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ReturnStatusHistoryDetail;
